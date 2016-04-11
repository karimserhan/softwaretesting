/**
 * Class for logging stuff
 */
public class Logger {
    public static final boolean DEBUG = true;
    public static final boolean LOG = true;

    public static void log(String msg) {
        if (LOG) {
            System.out.println(msg);
        }
    }

    public static void debug(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }
}
