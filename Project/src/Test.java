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
                        "0:x0\n" +
                        "4:x0\n" +
                        "5:x1,x2\n" +
                        "WRITE\n" +
                        "1:x2\n" +
                        "2:x0,x1\n" +
                        "3:x2\n" +
                        "4:x0\n" +
                        "5:x1\n" +
                        "8:x1\n" +
                        "MESSAGES\n" +
                        "4:1\n" +
                        "5:1\n" +
                        "7:0\n"
        );

        Map<Integer, List<Integer>> cc = comp.getConcurrentEvents();

        for (int i = 0; i < 1000; i++) {
            TraceGenerator generator = new TraceGenerator(3, 3, 3, 3, 3, 3, 1, 1, 3, 3);
            Computation computation = generator.generateTrace();
            App app = new App(false, true, true, true);
            List<Computation> computations = app.generateComputations(computation);
            Logger.log("Generated: " + computations.size() + " computations\n");
        }
    }
}
