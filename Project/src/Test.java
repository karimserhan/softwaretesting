import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Computation comp = Computation.parseComputation(
            "0:3\n" +
            "1:3\n" +
            "2:3\n" +
            "READ\n" +
            "6:x1,x2\n" +
            "8:x0\n" +
            "WRITE\n" +
            "0:x1\n" +
            "1:x2\n" +
            "3:x2\n" +
            "4:x0\n" +
            "7:x0\n" +
            "MESSAGES\n" +
            "1:8\n" +
            "5:6"
        );

        Map<Integer, List<Integer>> cc = comp.getConcurrentEvents();
        App app = new App(false, true, true, true);
        List<Computation> comps = app.generateComputations(comp);
        System.out.println("Done");
    }
}
