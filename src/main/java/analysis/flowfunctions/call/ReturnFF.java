package analysis.flowfunctions.call;

import analysis.data.DFF;
import analysis.flowfunctions.normal.FieldStoreAliasHandler;
import heros.FlowFunction;
import soot.Local;
import soot.jimple.StaticFieldRef;

import java.util.HashSet;
import java.util.Set;

public class ReturnFF implements FlowFunction<DFF> {

    private Local tgtLocal;
    private Local retLocal;
    private FieldStoreAliasHandler aliasHandler;

    public ReturnFF(Local tgtLocal, Local retLocal, FieldStoreAliasHandler aliasHandler) {
        this.tgtLocal = tgtLocal;
        this.retLocal = retLocal;
        this.aliasHandler = aliasHandler;
    }


    @Override
    public Set<DFF> computeTargets(DFF source) {
        Set<DFF> res = new HashSet<>();
        if (source.equals(DFF.asDFF(retLocal))) {
            res.add(DFF.asDFF(tgtLocal));
            aliasHandler.handleAliases(res);
        }
        if(source.getValue() instanceof StaticFieldRef){
            res.add(source);
        }
        return res;
    }
}
