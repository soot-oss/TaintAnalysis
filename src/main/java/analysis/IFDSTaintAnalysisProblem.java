package analysis;

import analysis.data.DFF;
import analysis.flowfunctions.CallFlowFunctionProvider;
import analysis.flowfunctions.NormalFlowFunctionProvider;
import analysis.flowfunctions.ReturnFlowFunctionProvider;
import analysis.flowfunctions.CallToReturnFlowFunctionProvider;
import heros.DefaultSeeds;
import heros.FlowFunction;
import heros.FlowFunctions;
import heros.InterproceduralCFG;
import soot.*;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import util.CFGUtil;

import java.util.*;

public class IFDSTaintAnalysisProblem extends DefaultJimpleIFDSTabulationProblem<DFF, InterproceduralCFG<Unit, SootMethod>> {

    private List<SootMethodRef> sources;
    private List<SootMethodRef> sinks;

    protected InterproceduralCFG<Unit, SootMethod> icfg;


    public IFDSTaintAnalysisProblem(InterproceduralCFG<Unit, SootMethod> icfg, List<SootMethodRef> sources, List<SootMethodRef> sinks) {
        super(icfg);
        this.icfg = icfg;
        this.sources = sources;
        this.sinks = sinks;
    }

    @Override
    protected FlowFunctions<Unit, DFF, SootMethod> createFlowFunctionsFactory() {
        return new FlowFunctions<Unit, DFF, SootMethod>() {
            @Override
            public FlowFunction<DFF> getNormalFlowFunction(Unit curr, Unit succ) {
                NormalFlowFunctionProvider ffp = new NormalFlowFunctionProvider(icfg.getMethodOf(curr), curr, zeroValue());
                return ffp.getFlowFunction();
            }

            @Override
            public FlowFunction<DFF> getCallFlowFunction(Unit callStmt, SootMethod dest) {
                CallFlowFunctionProvider ffp = new CallFlowFunctionProvider(callStmt, dest, zeroValue());
                return ffp.getFlowFunction();
            }

            @Override
            public FlowFunction<DFF> getReturnFlowFunction(Unit callSite, SootMethod calleeMethod, Unit exitStmt, Unit returnSite) {
                ReturnFlowFunctionProvider ffp = new ReturnFlowFunctionProvider(callSite, exitStmt, icfg.getMethodOf(callSite), icfg.getMethodOf(exitStmt));
                return ffp.getFlowFunction();
            }

            @Override
            public FlowFunction<DFF> getCallToReturnFlowFunction(Unit callSite, Unit returnSite) {
                CallToReturnFlowFunctionProvider ffp = new CallToReturnFlowFunctionProvider(icfg.getMethodOf(callSite), callSite, zeroValue(), sources);
                return ffp.getFlowFunction();
            }
        };
    }

    @Override
    protected DFF createZeroValue() {
        return DFF.asDFF(new JimpleLocal("<<zero>>", NullType.v()));
    }

    @Override
    public Map<Unit, Set<DFF>> initialSeeds() {
        for (SootClass c : Scene.v().getApplicationClasses()) {
            for (SootMethod m : c.getMethods()) {
                if (!m.hasActiveBody()) {
                    continue;
                }
                if (m.toString().contains("void main(java.lang.String[])")) {
                    DirectedGraph<Unit> unitGraph = new BriefUnitGraph(m.getActiveBody());
                    return DefaultSeeds.make(Collections.singleton(CFGUtil.getHead(unitGraph)), zeroValue());
                }
            }
        }
        throw new IllegalStateException("scene does not contain main method");
    }


}
