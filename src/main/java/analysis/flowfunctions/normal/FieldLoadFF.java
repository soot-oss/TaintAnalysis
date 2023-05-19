package analysis.flowfunctions.normal;

import analysis.data.DFF;
import heros.FlowFunction;
import soot.Value;
import soot.jimple.FieldRef;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FieldLoadFF implements FlowFunction<DFF> {

    private AliasHandler aliasHandler;
    private FieldRef fieldRef;
    private Value lhs;
    private DFF zeroValue;

    public FieldLoadFF(FieldRef fieldRef, Value lhs, DFF zeroValue, AliasHandler aliasHandler) {
        this.fieldRef = fieldRef;
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
        if(DFF.asDFF(fieldRef).equals(source)){
            res.add(DFF.asDFF(lhs));
            aliasHandler.handleAliases(res);
        }
        return res;
    }
}
