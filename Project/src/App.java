import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {
    Computation computation;
    private boolean merge;
    private boolean raw;
    private boolean war;
    private boolean waw;

    public App(Computation computation, boolean merge, boolean raw, boolean war, boolean waw) {
        this.computation = computation;
        this.merge = merge;
        this.raw = raw;
        this.war = war;
        this.waw = waw;
    }

    private List<Computation> generateComputationsForOneEntry(Computation comp, int fromId, int toIndex,
                                                              Set<Integer> toIds) {
        // TODO:
        if (toIndex == toIds.size()) {
            List<Computation> c = new ArrayList<>();
            c.add(comp);
            return c;
        }
    }

    public List<Computation> generateComputations() {
        Map<Integer, Set<Integer>> concurrentEvents = this.computation.getConcurrentEvents();
        // TODO:
    }
}
