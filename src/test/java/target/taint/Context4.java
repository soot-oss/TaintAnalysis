package target.taint;

import target.taint.internal.SourceClass;

public class Context4 {

    static void assign(String s, Field f){
        f.x = s;
        f.y = SourceClass.aStaticSource();
    }

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        Field f = new Field();
        assign(a, f);
        String b = f.x;
        String c = f.y;
    }


}
