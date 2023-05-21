package target.taint;

import org.checkerframework.checker.units.qual.C;
import target.taint.internal.SourceClass;

public class Context3 {

    String nested(String s){
        return nested1(s);
    }

    String nested1(String s){
        return s;
    }


    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        Context3 c = new Context3();
        String b = c.nested(a);
    }

}
