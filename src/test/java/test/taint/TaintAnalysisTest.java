package test.taint;

import analysis.IFDSTaintAnalysisProblem;
import analysis.data.DFF;
import heros.InterproceduralCFG;
import org.junit.Test;
import soot.*;
import soot.jimple.toolkits.ide.JimpleIFDSSolver;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import test.base.IFDSTestSetUp;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class TaintAnalysisTest extends IFDSTestSetUp {

    @Override
    protected Transformer createAnalysisTransformer() {
        List<SootMethodRef> sources = new ArrayList<>();
        List<SootMethodRef> sinks = new ArrayList<>();
        SootClass sourceClass = new SootClass("target.taint.internal.SourceClass");
        SootMethodRef source1 = new SootMethodRefImpl(sourceClass, "anInstanceSource", Collections.emptyList(), RefType.v("java.lang.String"), false);
        SootMethodRef source2 = new SootMethodRefImpl(sourceClass, "aStaticSource", Collections.emptyList(), RefType.v("java.lang.String"), true);

        SootClass sinkClass = new SootClass("target.taint.internal.SinkClass");
        SootMethodRef sink1 = new SootMethodRefImpl(sinkClass, "anInstanceSink", Collections.emptyList(), RefType.v("java.lang.String"), false);
        SootMethodRef sink2 = new SootMethodRefImpl(sinkClass, "aStaticSink", Collections.emptyList(), RefType.v("java.lang.String"), true);

        sources.add(source1);
        sources.add(source2);
        sinks.add(sink1);
        sinks.add(sink2);

        return new SceneTransformer() {
            @Override
            protected void internalTransform(String phaseName, Map<String, String> options) {
                JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG(false);
                IFDSTaintAnalysisProblem problem = new IFDSTaintAnalysisProblem(icfg, sources, sinks);
                @SuppressWarnings({"rawtypes", "unchecked"})
                JimpleIFDSSolver<?, ?> solver = new JimpleIFDSSolver<>(problem);
                solver.solve();
                IFDSTestSetUp.solver = solver;
            }
        };
    }


    private Set<String> getResult(Object analysis) {
        SootMethod m = getEntryPointMethod();
        Map<DFF, Integer> res = null;
        Set<String> result = new HashSet<>();
        if (analysis instanceof JimpleIFDSSolver) {
            JimpleIFDSSolver solver = (JimpleIFDSSolver) analysis;
            res = (Map<DFF, Integer>) solver.resultsAt(m.getActiveBody().getUnits().getLast());
        }
        for (Map.Entry<DFF, Integer> e : res.entrySet()) {
            result.add(e.getKey().toString());
        }
        return result;
    }

    private void checkResults(Set<String> defaultIDEResult, Set<String> expected) {
        // first remove intermediate vars
        Supplier<Predicate<String>> pred = () -> p -> !(p.startsWith("$stack") || p.startsWith("varReplacer"));
        defaultIDEResult = defaultIDEResult.stream().filter(pred.get()).collect(Collectors.toSet());
        assertTrue(msg(defaultIDEResult, expected), defaultIDEResult.containsAll(expected));
    }

    private String msg(Set<String> actual, Set<String> expected) {
        StringBuilder str = new StringBuilder(System.lineSeparator());
        str.append("actual:").append(System.lineSeparator());
        str.append(actual.stream().collect(Collectors.joining("-"))).append(System.lineSeparator());
        str.append("expected:").append(System.lineSeparator());
        str.append(expected.stream().collect(Collectors.joining("-"))).append(System.lineSeparator());
        return str.toString();
    }

    @Test
    public void Assignment() {
        JimpleIFDSSolver<?, ? extends InterproceduralCFG<Unit, SootMethod>> analysis = executeStaticAnalysis(target.taint.Assignment.class.getName());
        Set<String> defaultIDEResult = getResult(analysis);
        Set<String> expected = new HashSet<>();
        expected.add("a");
        expected.add("b");
        checkResults(defaultIDEResult, expected);
    }


}
