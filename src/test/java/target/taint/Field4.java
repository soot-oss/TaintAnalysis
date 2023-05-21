package target.taint;

import target.taint.internal.SourceClass;

public class Field4 {

    private String x;
    private String y;

    public static void main(String[] args) {
        Field4 f = new Field4();
        SourceClass sc = new SourceClass();
        f.x = sc.anInstanceSource();
        f.y = f.x;
    }

}
