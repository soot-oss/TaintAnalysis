package analysis.flowfunctions.normal;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JInstanceFieldRef;

public class AliasHandlerProvider {

    public static AliasHandler get(SootMethod method, Unit curr, Value lhs) {
        if (lhs instanceof JInstanceFieldRef) {
            return new FieldStoreAliasHandler(method, curr, lhs);
        } else if (lhs instanceof JArrayRef) {
            return new ArrayStoreAliasHandler(method, curr, lhs);
        } else {
            return new AliasHandler() {
            };
        }
    }


}
