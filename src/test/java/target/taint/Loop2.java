package target.taint;

import target.taint.internal.SourceClass;

public class Loop2 {


    public static void main(String[] args) {
        String a = "";
        while (true) {
            a = SourceClass.aStaticSource();
        }
    }

}
