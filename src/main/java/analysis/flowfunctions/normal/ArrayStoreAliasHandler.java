package analysis.flowfunctions.normal;

import aliasing.AliasManager;
import analysis.data.DFF;
import boomerang.scene.Val;
import boomerang.scene.jimple.JimpleVal;
import boomerang.util.AccessPath;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;

import java.util.Set;

public class ArrayStoreAliasHandler implements AliasHandler {

    private JArrayRef arrayRef;
    private Unit curr;
    private SootMethod method;

    public ArrayStoreAliasHandler(SootMethod method, Unit curr, Value lhs) {
        if (lhs instanceof JArrayRef) {
            this.arrayRef = (JArrayRef) lhs;
        }
        this.curr = curr;
        this.method = method;
    }

    @Override
    public void handleAliases(Set<DFF> res) {
        if (this.arrayRef != null) {
            AliasManager aliasManager = AliasManager.getInstance();
            Set<AccessPath> aliases = aliasManager.getAliases((Stmt) curr, method, arrayRef.getBase());
            for (AccessPath alias : aliases) {
                Val base = alias.getBase();
                if (base instanceof JimpleVal) {
                    JimpleVal jval = (JimpleVal) base;
                    Value delegate = jval.getDelegate();
                    if(!delegate.equals(arrayRef.getBase())){
                        JArrayRef newRef = new JArrayRef(delegate, arrayRef.getIndex());
                        res.add(new DFF(newRef, curr));
                    }
                }
            }
        }
    }

}
