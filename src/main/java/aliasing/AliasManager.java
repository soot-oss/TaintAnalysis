package aliasing;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.DefaultBoomerangOptions;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.SootDataFlowScope;
import boomerang.scene.Statement;
import boomerang.scene.jimple.JimpleMethod;
import boomerang.scene.jimple.JimpleStatement;
import boomerang.scene.jimple.JimpleVal;
import boomerang.scene.jimple.SootCallGraph;
import boomerang.util.AccessPath;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Scene;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Stmt;
import wpds.impl.Weight;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class AliasManager {

    private static Logger log = LoggerFactory.getLogger(AliasManager.class);

    private static AliasManager INSTANCE;

    private LoadingCache<BackwardQuery, Set<AccessPath>> queryCache;

    private Boomerang boomerangSolver;

    private SootCallGraph sootCallGraph;
    private DataFlowScope dataFlowScope;

    private boolean disableAliasing = false;


    static class BoomerangOptions extends DefaultBoomerangOptions {


        public BoomerangOptions() {

        }

        @Override
        public int analysisTimeoutMS() {
            return 1000;
        }

        @Override
        public boolean onTheFlyCallGraph() {
            return false;
        }

        @Override
        public StaticFieldStrategy getStaticFieldStrategy() {
            return StaticFieldStrategy.SINGLETON;
        }

        @Override
        public boolean allowMultipleQueries() {
            return true;
        }

        @Override
        public boolean throwFlows() {
            return true;
        }

        @Override
        public boolean trackAnySubclassOfThrowable() {
            return true;
        }
    }

    private static Duration totalAliasingDuration;

    private AliasManager() {
        totalAliasingDuration = Duration.ZERO;
        sootCallGraph = new SootCallGraph();
        dataFlowScope = SootDataFlowScope.make(Scene.v());
        setupQueryCache();
    }

    public static Duration getTotalDuration() {
        return totalAliasingDuration;
    }

    public static synchronized AliasManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AliasManager();
        }
        return INSTANCE;
    }

    private void setupQueryCache() {
        queryCache =
                CacheBuilder.newBuilder()
                        .build(
                                new CacheLoader<BackwardQuery, Set<AccessPath>>() {
                                    @Override
                                    public Set<AccessPath> load(BackwardQuery query) throws Exception {
                                        Set<AccessPath> aliases = queryCache.getIfPresent(query);
                                        if (aliases == null) {
                                            // TODO: stabilize null pointer exception that happens sometimes in boomerang
                                            boomerangSolver =
                                                    new Boomerang(
                                                            sootCallGraph, dataFlowScope, new BoomerangOptions());
                                            BackwardBoomerangResults<Weight.NoWeight> results = boomerangSolver.solve(query);
                                            aliases = results.getAllAliases();
                                            boolean debug = false;
                                            if (debug) {
                                                System.out.println(query);
                                                System.out.println("alloc:" + results.getAllocationSites());
                                                System.out.println("aliases:" + aliases);
                                            }//boomerangSolver.unregisterAllListeners();
                                            //boomerangSolver.unregisterAllListeners();
                                            queryCache.put(query, aliases);
                                        }
                                        return aliases;
                                    }
                                });
    }


    /**
     * @param stmt   Statement that contains the value. E.g. Value can be the leftOp
     * @param method Method that contains the Stmt
     * @param value  We actually want to find this local's aliases
     * @return
     */
    public synchronized Set<AccessPath> getAliases(Stmt stmt, SootMethod method, Value value) {
        //log.info(method.getActiveBody().toString());
        //log.info("getAliases call for: " + stmt + " in " + method);
        if (disableAliasing) {
            return Collections.emptySet();
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        BackwardQuery query = createQuery(stmt, method, value);
        Set<AccessPath> aliases = getAliases(query);
        Duration elapsed = stopwatch.elapsed();
        totalAliasingDuration = totalAliasingDuration.plus(elapsed);
        return aliases;
    }

    private BackwardQuery createQuery(Stmt stmt, SootMethod method, Value value) {
        JimpleMethod jimpleMethod = JimpleMethod.of(method);
        Statement statement = JimpleStatement.create(stmt, jimpleMethod);
        JimpleVal val = new JimpleVal(value, jimpleMethod);
        Optional<Statement> first = statement.getMethod().getControlFlowGraph().getSuccsOf(statement).stream().findFirst();
        if (first.isPresent()) {
            return BackwardQuery.make(new ControlFlowGraph.Edge(statement, first.get()), val);
        }
        throw new RuntimeException("No successors for: " + statement);
    }

    private Set<AccessPath> getAliases(BackwardQuery query) {
        try {
            return queryCache.get(query);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }


}
