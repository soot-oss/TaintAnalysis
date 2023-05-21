package target.taint;

import target.taint.internal.SourceClass;

public class Loop {


    public static void main(String[] args) {
        String a = "";
        for (int i = 0; i < 5; ++i) {
            a = SourceClass.aStaticSource();
        }
    }

}
