import java.util.Collection;

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
}
