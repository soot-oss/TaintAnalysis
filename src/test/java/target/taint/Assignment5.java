package target.taint;

import target.taint.internal.SourceClass;

public class Assignment5 {

    private static String a;

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        a = sc.anInstanceSource();
        String b = a;
    }

}
