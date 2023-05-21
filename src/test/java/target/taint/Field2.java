package target.taint;

import target.taint.internal.SourceClass;

public class Field2 {

    private String x;
    private String y;

    public static void main(String[] args) {
        Field2 f = new Field2();
        SourceClass sc = new SourceClass();
        f.x = sc.anInstanceSource();
        String a = f.x;
    }

}
