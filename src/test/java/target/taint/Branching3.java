package target.taint;

import target.taint.internal.SourceClass;

public class Branching3 {

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = sc.anInstanceSource();
        if(args[0]==""){
            a = "";
        } else{
            a = sc.anInstanceSource();
        }
        String b = a;
    }

}
