package target.taint;

import target.taint.internal.SourceClass;

public class Branching2 {

    public static void main(String[] args) {
        SourceClass sc = new SourceClass();
        String a = "";
        if(args[0]==""){
            a = sc.anInstanceSource();
        } else{
            a = sc.anInstanceSource();
        }
        String b = a;
    }

}
