package analysis.flowfunctions;


import analysis.data.DFF;
import analysis.flowfunctions.normal.*;
import heros.FlowFunction;
import heros.flowfunc.Identity;
import heros.flowfunc.Kill;
import heros.flowfunc.KillAll;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.internal.JArrayRef;


public class NormalFlowFunctionProvider implements FlowFunctionProvider<DFF> {

    private FlowFunction<DFF> flowFunction;

    public NormalFlowFunctionProvider(SootMethod method, Unit curr, DFF zeroValue) {
        flowFunction = Identity.v(); // always id as fallback
        if (curr instanceof DefinitionStmt) {
            DefinitionStmt assignment = (DefinitionStmt) curr;
            Value lhs = assignment.getLeftOp();
            Value rhs = assignment.getRightOp();
            if (rhs instanceof Local) {
                // assignment of local
                Local right = (Local) rhs;
                flowFunction = new LocalFF(right, lhs, zeroValue, AliasHandlerProvider.get(method, curr, lhs));
            } else if (rhs instanceof FieldRef) {
                // assignment of instance field
                FieldRef fieldRef = (FieldRef) rhs;
                flowFunction = new FieldLoadFF(fieldRef, lhs, zeroValue, AliasHandlerProvider.get(method, curr, lhs));
            } else if (rhs instanceof JArrayRef) {
                JArrayRef arrRef = (JArrayRef) rhs;
                flowFunction = new ArrayLoadFF(arrRef, lhs, zeroValue, AliasHandlerProvider.get(method, curr, lhs));
            } else if(rhs instanceof Constant){
                flowFunction = new KillFF(lhs, zeroValue);
            }
        }
    }

    public FlowFunction<DFF> getFlowFunction() {
        return flowFunction;
    }

}
