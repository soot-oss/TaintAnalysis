package target.taint;

import target.taint.internal.SourceClass;

public class Assignment4 {

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        String b = a;
        String c = b;
        String d = c;
        String e = c;
    }

}
