import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {
    private boolean merge;
    private boolean raw;
    private boolean war;
    private boolean waw;

    public App(boolean merge, boolean raw, boolean war, boolean waw) {
        this.merge = merge;
        this.raw = raw;
        this.war = war;
        this.waw = waw;
    }

    public List<Computation> generateComputations(Computation computation) {
        Map<Integer, List<Integer>> concurrentEvents = computation.getConcurrentEvents();

        List<Computation> result = new ArrayList<>();
        result.add(new Computation()); // add empty computation for 1st cross product merge to work

        for (Map.Entry<Integer, List<Integer>> entry : concurrentEvents.entrySet()) {
            int fromId = entry.getKey();
            List<Integer> toIds = entry.getValue();
            List<Computation> list = generateComputationsForOneEntry(computation, fromId, toIds, 0);
            result = mergeComputationLists(result, list);
        }

        return result;
    }

    private List<Computation> generateComputationsForOneEntry(Computation computation, int fromId,
                                                              List<Integer> toIds, int toIndex) {
        if (toIndex == toIds.size()) {
            List<Computation> c = new ArrayList<>();
            c.add(computation);
            return c;
        }

        Computation dupComp1 = new Computation(computation);
        Computation dupComp2 = new Computation(computation);

        dupComp1.addMessage(fromId, toIds.get(toIndex));
        dupComp2.addMessage(toIds.get(toIndex), fromId);

        List<Computation> list1 = generateComputationsForOneEntry(dupComp1, fromId, toIds, toIndex + 1);
        List<Computation> list2 = generateComputationsForOneEntry(dupComp2, fromId, toIds, toIndex + 1);
        list1.addAll(list2);

        return list1;
    }

    private List<Computation> mergeComputationLists(List<Computation> list1, List<Computation> list2) {
        List<Computation> result = new ArrayList<>();

        for (Computation comp1 : list1) {
            for (Computation comp2 : list2) {
                comp1.mergeWith(comp2);
                result.add(comp1);
            }
        }

        return result;
    }
}
