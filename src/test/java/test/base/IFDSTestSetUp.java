package test.base;

import boomerang.scene.jimple.BoomerangPretransformer;
import soot.*;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import soot.options.Options;

import java.io.File;

public abstract class IFDSTestSetUp {

    protected static JimpleIFDSSolver<?, ?> solver = null;

    protected JimpleIFDSSolver<?, ?> executeStaticAnalysis(String targetTestClassName) {
        setupSoot(targetTestClassName);
        registerSootTransformers();
        executeSootTransformers();
        if (solver == null) {
            throw new NullPointerException("Something went wrong solving the IFDS problem!");
        }
        return solver;
    }

    private void executeSootTransformers() {
        //Apply all necessary packs of soot. This will execute the respective Transformer
        PackManager.v().getPack("cg").apply();
        // Must have for Boomerang
        BoomerangPretransformer.v().reset();
        BoomerangPretransformer.v().apply();
        PackManager.v().getPack("wjtp").apply();
    }

    private void registerSootTransformers() {
        Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
        PackManager.v().getPack("wjtp").add(transform);
    }

    protected abstract Transformer createAnalysisTransformer();

    /*
	 * This method provides the options to soot to analyse the respecive
	 * classes.
     */
    private void setupSoot(String targetTestClassName) {
        G.reset();
        String userdir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");

		String sootCp = userdir + File.separator + "target" + File.separator + "test-classes"+ File.pathSeparator + javaHome + File.separator +"lib"+File.separator+"rt.jar";
		Options.v().set_soot_classpath(sootCp);

        // We want to perform a whole program, i.e. an interprocedural analysis.
        // We construct a basic CHA call graph for the program
        Options.v().set_whole_program(true);
        Options.v().setPhaseOption("cg.cha", "on");
        Options.v().setPhaseOption("cg", "all-reachable:true");

        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("jb.ls", "enabled:false");
		Options.v().set_prepend_classpath(false);

        Scene.v().addBasicClass("java.lang.StringBuilder");
        SootClass c = Scene.v().forceResolve(targetTestClassName, SootClass.BODIES);
        if (c != null) {
            c.setApplicationClass();
        }
        Scene.v().loadNecessaryClasses();
    }


    protected SootMethod getEntryPointMethod() {
        for (SootClass c : Scene.v().getApplicationClasses()) {
            for (SootMethod m : c.getMethods()) {
                if (!m.hasActiveBody()) {
                    continue;
                }
                if (m.getName().equals("entryPoint") || m.toString().contains("void main(java.lang.String[])")) {
                    return m;
                }
            }
        }
        throw new IllegalArgumentException("Method does not exist in scene!");
    }

}
