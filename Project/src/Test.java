import java.util.List;

public class Test {
    public static void main(String[] args) {
        for (int i = 0; i < 0; i++) {
            Computation comp = Computation.parseComputation(
                    "0:3\n" +
                            "1:3\n" +
                            "2:3\n" +
                            "READ\n" +
                            "WRITE\n" +
                            "MESSAGES\n" +
                            "6:5"
            );
            boolean b = comp.addMessage(3,2);
            App app = new App(false, true, false, false);
            List<Computation> computations = app.generateComputations(comp);
        }
        //if(true) return;
        for (int i = 0; i < 1; i++) {
            TraceGenerator generator = new TraceGenerator(3, 3, 3, 3, 3, 3, 3, 3, 3, 3);
            Computation computation = generator.generateTrace();
            App app = new App(false, true, false, false);
            List<Computation> computations = app.generateComputations(computation);
        }
    }
}
