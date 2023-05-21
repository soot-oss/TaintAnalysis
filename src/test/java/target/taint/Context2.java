package target.taint;

import target.taint.internal.SourceClass;

public class Context2 {

    static String sourceInside() {
        SourceClass sc = new SourceClass();
        return sc.anInstanceSource();
    }

    public static void main(String[] args) {
        String a = sourceInside();
    }

}
