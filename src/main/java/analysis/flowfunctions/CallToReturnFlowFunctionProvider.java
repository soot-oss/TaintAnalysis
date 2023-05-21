package analysis.flowfunctions;

import analysis.data.DFF;
import analysis.flowfunctions.call.KillStaticCTRFF;
import analysis.flowfunctions.normal.AliasHandlerProvider;
import analysis.flowfunctions.normal.SourceFF;
import heros.FlowFunction;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;

import java.util.List;

public class CallToReturnFlowFunctionProvider implements FlowFunctionProvider<DFF> {

    private FlowFunction<DFF> flowFunction;

    public CallToReturnFlowFunctionProvider(SootMethod method, Unit curr, DFF zeroValue, List<SootMethodRef> sources) {
        if (curr instanceof DefinitionStmt) {
            DefinitionStmt def = (DefinitionStmt) curr;
            Value lhs = def.getLeftOp();
            Value rhs = def.getRightOp();
            if (rhs instanceof InvokeExpr) {
                InvokeExpr invoke = (InvokeExpr) rhs;
                SootMethodRef methodRef = invoke.getMethodRef();
                if (sources.contains(methodRef)) {
                    flowFunction = new SourceFF(new DFF(lhs, curr), zeroValue, AliasHandlerProvider.get(method, curr, lhs));
                    return;
                }
            }
        }
        flowFunction = new KillStaticCTRFF();

    }

    @Override
    public FlowFunction<DFF> getFlowFunction() {
        return flowFunction;
    }
}
