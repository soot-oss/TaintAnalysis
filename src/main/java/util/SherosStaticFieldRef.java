package util;

import soot.SootFieldRef;
import soot.jimple.StaticFieldRef;

/**
 * Only because StaticFieldRef's constructor is protected
 */
public class SherosStaticFieldRef extends StaticFieldRef {
    public SherosStaticFieldRef(SootFieldRef fieldRef) {
        super(fieldRef);
    }
}
