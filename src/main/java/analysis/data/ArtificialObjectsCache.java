package analysis.data;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.Value;
import soot.jimple.internal.JArrayRef;

import java.util.HashMap;
import java.util.Map;

/**
 * Newly creates SootClasses or SootFields do not equal, because of numbering. Here we cache classes and fields ignoring the numbering.
 */
public class ArtificialObjectsCache {

    public static Map<String, SootClass> classes = new HashMap<>();

    public static Map<String, SootField> fields = new HashMap<>();

    public static SootClass getSootClass(Value base) {
        SootClass sc = Scene.v().makeSootClass(base.toString());
        String str = sootClassString(sc);
        if (classes.containsKey(str)) {
            return classes.get(str);
        } else {
            classes.put(str, sc);
            return sc;
        }
    }

    public static SootField getSootField(JArrayRef arr) {
        Value base = arr.getBase();
        Value index = arr.getIndex();
        SootClass sc = getSootClass(base);
        SootField sootField = Scene.v().makeSootField("i_" + index.toString(), arr.getType());
        sootField.setDeclaringClass(sc);
        sootField.setDeclared(true);
        String str = sootFieldString(sootField);
        if (fields.containsKey(str)) {
            return fields.get(str);
        } else {
            fields.put(str, sootField);
            return sootField;
        }
    }

    private static String sootFieldString(SootField sf) {
        StringBuilder str = new StringBuilder();
        str.append(sf.getDeclaringClass().toString()).append(sf.getType().toString()).append(sf.getModifiers()).append(sf.getSignature()).append(sf.getName());
        return str.toString();
    }

    private static String sootClassString(SootClass sc) {
        StringBuilder str = new StringBuilder();
        str.append(sc.getType()).append(sc.getName());
        return str.toString();
    }

}
