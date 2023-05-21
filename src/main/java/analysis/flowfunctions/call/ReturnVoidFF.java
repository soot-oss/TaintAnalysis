package analysis.flowfunctions.call;

import analysis.data.DFF;
import heros.FlowFunction;
import heros.solver.Pair;
import soot.*;
import soot.jimple.InvokeStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReturnVoidFF implements FlowFunction<DFF> {
    private Unit callsite;
    private SootMethod method;

    public ReturnVoidFF(Unit callsite, SootMethod method) {
        this.callsite = callsite;
        this.method = method;
    }


    @Override
    public Set<DFF> computeTargets(DFF source) {
        callsite.toString();
        Set<DFF> res = new HashSet<>();
        Value d = source.getValue();
        if(d instanceof JInstanceFieldRef){
            if(callsite instanceof InvokeStmt){
                InvokeStmt invoke = (InvokeStmt) callsite;
                List<Value> args = invoke.getInvokeExpr().getArgs();
                JInstanceFieldRef fieldRef = (JInstanceFieldRef) d;
                Value base = fieldRef.getBase();
                int argIndex = 0;
                for (Value arg : args) {
                    Pair<Value, Integer> mArg = new Pair<>(arg, argIndex);
                    if(isSameParam(method, mArg, base)){
                        JInstanceFieldRef mapRef = new JInstanceFieldRef(arg, fieldRef.getFieldRef());
                        res.add(DFF.asDFF(mapRef));
                    }
                    argIndex++;
                }
            }
        }
        if(d instanceof StaticFieldRef){
            res.add(source);
        }
        return res;
    }

    boolean isSameParam(SootMethod method, Pair<Value, Integer> actualParam, Value formalParam){
        if(actualParam.getO1().getType() instanceof RefType){
            Body activeBody = method.getActiveBody();
            UnitPatchingChain units = activeBody.getUnits();
            int idIndex = method.isStatic() ? 0 : -1; // @this is only in virtual invoke
            for (Unit unit : units) {
                if(unit instanceof JIdentityStmt){
                    JIdentityStmt id = (JIdentityStmt) unit;
                    Value rightOp = id.getRightOp();
                    Value leftOp = id.getLeftOp();
                    if(rightOp.getType().equals(actualParam.getO1().getType()) && leftOp.equals(formalParam) && actualParam.getO2().equals(idIndex)){
                        return true;
                    }
                    idIndex++;
                }
            }
        }
        return false;
    }

}
