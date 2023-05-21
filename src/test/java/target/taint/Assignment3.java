package target.taint;

import target.taint.internal.SourceClass;

public class Assignment3 {

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        String b = a;
        a = "";
    }

}
