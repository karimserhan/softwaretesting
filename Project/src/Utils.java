import java.util.*;
import java.util.function.BooleanSupplier;

/**
 * Class for utility functions
 */
public class Utils {
    public static<E> String joinCollection(Collection<E> list, String joinWith) {
        String output = "";
        String delimeter = "";

        for (E elt : list) {
            output += delimeter + elt.toString();
            delimeter = joinWith;
        }

        return output;
    }

    public static Map<Integer,Set<Integer>> copyMapOfSets(Map<Integer, Set<Integer>> source) {
        Map<Integer,Set<Integer>> copy = new HashMap<>();
        for (Map.Entry<Integer,Set<Integer>> entry : source.entrySet()) {
            copy.put(entry.getKey(), new HashSet(entry.getValue()));
        }
        return copy;
    }

    public static Map<Integer,Event> copyMapOfEvents(Map<Integer, Event> source) {
        Map<Integer,Event> copy = new HashMap<>();
        for (Map.Entry<Integer,Event> entry : source.entrySet()) {
            copy.put(entry.getKey(), new Event(entry.getValue()));
        }
        return copy;
    }

    public static Map<Integer, List<Integer>> copyMapOfLists(Map<Integer, List<Integer>> source) {
        Map<Integer,List<Integer>> copy = new HashMap<>();
        for (Map.Entry<Integer,List<Integer>> entry : source.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    public static Map<String, Boolean> copyReachabilityCache(Map<String, Boolean> source) {
        Map<String,Boolean> copy = new HashMap<>();
        for (Map.Entry<String,Boolean> entry : source.entrySet()) {
            copy.put(new String(entry.getKey()), new Boolean(entry.getValue()));
        }
        return copy;
    }
}
