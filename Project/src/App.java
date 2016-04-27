import java.util.*;

public class App {
    private boolean merge;
    private boolean raw;
    private boolean war;
    private boolean waw;
    private Set<Computation> result;

    public App(boolean merge, boolean raw, boolean war, boolean waw) {
        this.merge = merge;
        this.raw = raw;
        this.war = war;
        this.waw = waw;
    }

    public List<Computation> generateComputations(Computation computation) {
        Map<Integer, List<Integer>> concurrentEvents = computation.getConcurrentEvents();

        result = new HashSet<>();
        result.add(new Computation(computation)); // add original computation for 1st cross product merge to work

        for (Map.Entry<Integer, List<Integer>> entry : concurrentEvents.entrySet()) {
            int fromId = entry.getKey();
            List<Integer> toIds = entry.getValue();
            Set<Computation> set = generateComputationsForOneEntry(new Computation(computation), fromId, toIds, 0);
            result = mergeComputationSets(result, set);
        }

        // TODO: Try to fix later
        Set<Computation> finalResult = new HashSet<>();
        for (Computation comp : result) {
            if (!finalResult.contains(comp) && !comp.equals(computation)) {
                finalResult.add(comp);
            }
        }

        return new ArrayList<>(finalResult);
    }

    private Set<Computation> generateComputationsForOneEntry(Computation computation, int fromId,
                                                              List<Integer> toIds, int toIndex) {
        if (toIndex == toIds.size()) {
            Set<Computation> c = new HashSet<>();
            c.add(computation);
            return c;
        }

        Set<Computation> set1 = new HashSet<>();
        Set<Computation> set2 = new HashSet<>();
        Set<Computation> set3 = new HashSet<>();

        boolean satisfiesFilter1 = false;
        boolean satisfiesFilter2 = false;

        Computation dupComp1 = new Computation(computation);
        Computation dupComp2 = new Computation(computation);
        Computation dupComp3 = new Computation(computation);

        if (satisfiesFilter(computation, fromId, toIds.get(toIndex))) {
            satisfiesFilter1 = dupComp1.addSyncMessage(fromId, toIds.get(toIndex));
            if (satisfiesFilter1) {
                set1 = generateComputationsForOneEntry(dupComp1, fromId, toIds, toIndex + 1);
            }
        }

        if (satisfiesFilter(computation, toIds.get(toIndex), fromId)) {
            satisfiesFilter2 = dupComp2.addSyncMessage(toIds.get(toIndex), fromId);
            if (satisfiesFilter2) {
                set2 = generateComputationsForOneEntry(dupComp2, fromId, toIds, toIndex + 1);
            }
        }

        if (!satisfiesFilter1 && !satisfiesFilter2) {
            set3 = generateComputationsForOneEntry(dupComp3, fromId, toIds, toIndex + 1);
        }

        set1.addAll(set2);
        set1.addAll(set3);

        return set1;
    }

    private boolean satisfiesFilter(Computation computation, int e1, int e2) {
        Set<String> writesOnE1 = computation.getEventById(e1).getWriteVariables();
        Set<String> writesOnE2 = computation.getEventById(e2).getWriteVariables();
        Set<String> readsOnE1 = computation.getEventById(e1).getReadVariables();
        Set<String> readsOnE2 = computation.getEventById(e2).getReadVariables();

        boolean hasRawDependency = readsOnE2.stream().anyMatch(x -> writesOnE1.contains(x));
        boolean hasWawDependency = writesOnE2.stream().anyMatch(x -> writesOnE1.contains(x));
        boolean hasWarDependency = writesOnE2.stream().anyMatch(x -> readsOnE1.contains(x));

        boolean dep = (hasRawDependency && raw) || (hasWarDependency && war) || (hasWawDependency && waw);
        return dep;
    }

    private Set<Computation> mergeComputationSets(Set<Computation> set1, Set<Computation> set2) {
        Set<Computation> result = new HashSet<>();

        for (Computation comp1 : set1) {
            for (Computation comp2 : set2) {
                result.add(comp1.mergeWith(comp2));
                result.add(comp2.mergeWith(comp1));
            }
        }

        return result;
    }
}
