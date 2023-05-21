package target.taint;

import target.taint.internal.SourceClass;

public class Branching {

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = "";
        if(args[0]==""){
            a = sc.anInstanceSource();
        }
        String b = a;
    }

}
