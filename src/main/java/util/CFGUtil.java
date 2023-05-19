package util;

import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.toolkits.graph.DirectedGraph;

import java.util.ArrayList;
import java.util.List;

public class CFGUtil {

    public static Unit getHead(DirectedGraph<Unit> graph) {
        List<Unit> heads = graph.getHeads();
        List<Unit> res = new ArrayList<>();
        for (Unit head : heads) {
            if (head instanceof IdentityStmt || graph.getSuccsOf(head).isEmpty()) {
                continue;
            }
            res.add(head);
        }
        if (res.size() > 1) {
            throw new RuntimeException("multiple heads!");
        }
        return res.get(0);
    }
}
