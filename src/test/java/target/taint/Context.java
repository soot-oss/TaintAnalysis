package target.taint;

import target.taint.internal.SourceClass;

public class Context {

    static String id(String s) {
        return s;
    }

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        String b = id(a);
    }

}
