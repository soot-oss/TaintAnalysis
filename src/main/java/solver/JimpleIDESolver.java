package solver;

import com.google.common.collect.Table;
import heros.IDETabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IDESolver;
import heros.solver.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootMethod;
import soot.Unit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JimpleIDESolver<D, V, I extends InterproceduralCFG<Unit, SootMethod>> extends IDESolver<Unit, D, SootMethod, V, I> {
    private static final Logger logger = LoggerFactory.getLogger(soot.jimple.toolkits.ide.JimpleIDESolver.class);

    private static final String OUT_PUT_DIR = "./out";

    public JimpleIDESolver(IDETabulationProblem<Unit, D, SootMethod, V, I> problem) {
        super(problem);
    }

    public void solve(String targetClassName) {
        super.solve();
        //this.dumpResults(targetClassName);
    }

    private static List<Pair<String, Set<String>>> checked = new ArrayList<>();

    public void addFinalResults(String entryMethod) {
        Iterator iter = this.val.cellSet().iterator();
        while (iter.hasNext()) {
            Table.Cell<Unit, D, ?> entry = (Table.Cell) iter.next();
            SootMethod method = this.icfg.getMethodOf(entry.getRowKey());
            Unit lastStmt = method.getActiveBody().getUnits().getLast();
            Set<String> results = new TreeSet<>();
            Map<D, V> res = this.resultsAt(lastStmt);
            for (Map.Entry<D, V> e : res.entrySet()) {
                if(!e.getKey().toString().contains("$stack") && !e.getKey().toString().contains("varReplacer")){
                    results.add(e.getKey().toString() + " - " + e.getValue());
                }
            }
            if(!results.isEmpty()){
                Pair pair = new Pair(method.getSignature(), results);
                if(!checked.contains(pair)){
                    checked.add(pair);
                }
            }
        }
    }

    public void dumpResults(String targetClassName) {
        File dir = new File(OUT_PUT_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(OUT_PUT_DIR + File.separator + "default-" + targetClassName + ".csv");
        try (FileWriter writer = new FileWriter(file, true)) {
            for (Pair<String, Set<String>> pair : checked) {
                for(String res: pair.getO2()){
                    String str = pair.getO1() + ";" + res + System.lineSeparator();
                    writer.write(str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
