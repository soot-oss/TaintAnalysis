package analysis.flowfunctions;


import analysis.data.DFF;
import analysis.flowfunctions.call.ReturnFF;
import analysis.flowfunctions.call.ReturnVoidFF;
import analysis.flowfunctions.normal.FieldStoreAliasHandler;
import heros.FlowFunction;
import heros.flowfunc.KillAll;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;


public class ReturnFlowFunctionProvider implements FlowFunctionProvider<DFF> {

    private FlowFunction<DFF> flowFunction;

    public ReturnFlowFunctionProvider(Unit callSite, Unit exitStmt, SootMethod caller, SootMethod callee){
        flowFunction = KillAll.v(); // we want to kill everything else when returning from a nested context
        if (exitStmt instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt) exitStmt;
            Value op = returnStmt.getOp();
            if (op instanceof Local) {
                if (callSite instanceof DefinitionStmt) {
                    DefinitionStmt defnStmt = (DefinitionStmt) callSite;
                    Value leftOp = defnStmt.getLeftOp();
                    if (leftOp instanceof Local) {
                        final Local tgtLocal = (Local) leftOp;
                        final Local retLocal = (Local) op;
                        flowFunction = new ReturnFF(tgtLocal, retLocal, new FieldStoreAliasHandler(caller, callSite, tgtLocal));
                    }
                }
            }
        }else if(exitStmt instanceof JReturnVoidStmt){
            flowFunction = new ReturnVoidFF(callSite, callee);
        }
    }

    public FlowFunction<DFF> getFlowFunction(){
        return flowFunction;
    }

}
