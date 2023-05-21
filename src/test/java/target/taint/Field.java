package target.taint;

import target.taint.internal.SourceClass;

public class Field {

    String x;
    String y;

    public static void main(String[] args) {
        Field f = new Field();
        SourceClass sc = new SourceClass();
        f.x = sc.anInstanceSource();
        f.y = SourceClass.aStaticSource();
    }

}
