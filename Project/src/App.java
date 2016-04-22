import java.util.*;

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
        result.add(new Computation(computation)); // add original computation for 1st cross product merge to work

        for (Map.Entry<Integer, List<Integer>> entry : concurrentEvents.entrySet()) {
            int fromId = entry.getKey();
            List<Integer> toIds = entry.getValue();
            List<Computation> list = generateComputationsForOneEntry(new Computation(computation), fromId, toIds, 0);
            result = mergeComputationLists(result, list);
        }

        return result;
    }

    private List<Computation> generateComputationsForOneEntry(Computation computation, int fromId,
                                                              List<Integer> toIds, int toIndex) {
        if (toIndex == toIds.size()) {
            List<Computation> c = new LinkedList<>();
            c.add(computation);
            return c;
        }

        if (toIndex == 2 && toIds.get(toIndex) == 6) {
            int x = 6;
        }

        List<Computation> list1 = new LinkedList<>();
        List<Computation> list2 = new LinkedList<>();
        List<Computation> list3 = new LinkedList<>();

        boolean satisfiesFilter1 = false;
        boolean satisfiesFilter2 = false;

        Computation dupComp1 = new Computation(computation);
        Computation dupComp2 = new Computation(computation);
        Computation dupComp3 = new Computation(computation);

        if (satisfiesFilter(computation, fromId, toIds.get(toIndex))) {
            satisfiesFilter1 = dupComp1.addSyncMessage(fromId, toIds.get(toIndex));
            if (satisfiesFilter1) {
                list1 = generateComputationsForOneEntry(dupComp1, fromId, toIds, toIndex + 1);
            }
        }

        if (satisfiesFilter(computation, toIds.get(toIndex), fromId)) {
            satisfiesFilter2 = dupComp2.addSyncMessage(toIds.get(toIndex), fromId);
            if (satisfiesFilter2) {
                list2 = generateComputationsForOneEntry(dupComp2, fromId, toIds, toIndex + 1);
            }
        }

        if (!satisfiesFilter1 && !satisfiesFilter2) {
            list3 = generateComputationsForOneEntry(dupComp3, fromId, toIds, toIndex + 1);
        }

        list1.addAll(list2);
        list1.addAll(list3);

        return list1;
    }

    private boolean satisfiesFilter(Computation computation, int e1, int e2) {
        Set<String> writesOnE1 = computation.getEventById(e1).getWriteVariables();
        Set<String> writesOnE2 = computation.getEventById(e2).getWriteVariables();
        Set<String> readsOnE1 = computation.getEventById(e1).getReadVariables();
        Set<String> readsOnE2 = computation.getEventById(e2).getReadVariables();

        boolean hasRawDependency = readsOnE2.stream().anyMatch(x -> writesOnE1.contains(x));
        boolean hasWawDependency = writesOnE2.stream().anyMatch(x -> writesOnE1.contains(x));
        boolean hasWarDependency = writesOnE2.stream().anyMatch(x -> readsOnE1.contains(x));

        return (hasRawDependency && raw) || (hasWarDependency && war) || (hasWawDependency && waw);
    }

    private List<Computation> mergeComputationLists(List<Computation> list1, List<Computation> list2) {
        List<Computation> result = new ArrayList<>();

        for (Computation comp1 : list1) {
            for (Computation comp2 : list2) {
                Computation comp = comp1.mergeWith(comp2);
                result.add(comp);
            }
        }

        return result;
    }
}
