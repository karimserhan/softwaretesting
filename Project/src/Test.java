import java.util.List;

public class Test {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            TraceGenerator generator = new TraceGenerator(3, 3, 3, 3, 3, 3, 1, 1, 3, 3);
            Computation computation = generator.generateTrace();
            App app = new App(false, true, true, true);
            List<Computation> computations = app.generateComputations(computation);
            Logger.log("Generated: " + computations.size() + " computations\n");
        }
    }
}
