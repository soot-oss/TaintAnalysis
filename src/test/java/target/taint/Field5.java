package target.taint;

import target.taint.internal.SourceClass;

public class Field5 {

    private String x;
    private String y;

    public static void main(String[] args) {
        Field5 f = new Field5();
        Field5 alias = f;
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        f.x = a;
    }

}
