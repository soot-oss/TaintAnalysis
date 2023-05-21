package target.taint;

import target.taint.internal.SourceClass;

public class Context5 {

    private static String a;

    static void assign(){
        a = SourceClass.aStaticSource();
    }

    public static void main(String[] args) {
        assign();
        String b = a;
    }


}
