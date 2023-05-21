package target.taint;

import target.taint.internal.SourceClass;

public class Field3 {

    private String x;
    private String y;

    public static void main(String[] args) {
        Field3 f = new Field3();
        Field3 alias = f;
        SourceClass sc = new SourceClass();
        f.x = sc.anInstanceSource();
    }

}
