package analysis.flowfunctions.normal;

import analysis.data.DFF;
import heros.FlowFunction;
import soot.Value;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Assignment from a single local
 */
public class KillFF implements FlowFunction<DFF> {

    private Value lhs;
    private DFF zeroValue;

    public KillFF(Value lhs, DFF zeroValue) {
        this.lhs = lhs;
        this.zeroValue = zeroValue;
    }


    @Override
    public Set<DFF> computeTargets(DFF source) {
        if (source.equals(zeroValue)) {
            return Collections.singleton(source);
        }
        Set<DFF> res = new HashSet<>();
        if (!DFF.asDFF(lhs).equals(source)) {
            res.add(source);
        }
        return res;
    }


}
