package analysis.flowfunctions.normal;

import analysis.data.DFF;
import heros.FlowFunction;
import soot.Local;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.internal.JArrayRef;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Assignment from a single local
 */
public class LocalFF implements FlowFunction<DFF> {

    private Local right;
    private Value lhs;
    private DFF zeroValue;
    private AliasHandler aliasHandler;

    public LocalFF(Local right, Value lhs, DFF zeroValue, AliasHandler aliasHandler) {
        this.right = right;
        this.lhs = lhs;
        this.zeroValue = zeroValue;
        this.aliasHandler = aliasHandler;
    }


    @Override
    public Set<DFF> computeTargets(DFF source) {
        if(source.equals(zeroValue)){
            return Collections.singleton(source);
        }
        Set<DFF> res = new HashSet<>();
        res.add(source);
        if (DFF.asDFF(right).equals(source)) {
            res.add(DFF.asDFF(lhs));
            aliasHandler.handleAliases(res);
        }
        // for arrays
        if(source.getValue() instanceof JArrayRef){
            JArrayRef arrayRef = (JArrayRef) source.getValue();
            if(arrayRef.getBase().equals(right)){
                if(!(lhs instanceof FieldRef)){
                    JArrayRef newRef = new JArrayRef(lhs, arrayRef.getIndex());
                    res.add(DFF.asDFF(newRef));
                    aliasHandler.handleAliases(res);
                }
            }
        }
        return res;
    }


}
