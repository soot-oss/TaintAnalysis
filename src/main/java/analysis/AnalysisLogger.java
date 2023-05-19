package analysis;

import soot.SootMethod;
import soot.Unit;

public class AnalysisLogger {

    private SootMethod currentMethod;
    private Unit currentStmt;


    public AnalysisLogger(SootMethod currentMethod, Unit currentStmt) {
        this.currentMethod = currentMethod;
        this.currentStmt = currentStmt;
    }

    public void log(){
        System.out.println(currentMethod.getSignature());
        System.out.println(currentStmt);
    }

}
