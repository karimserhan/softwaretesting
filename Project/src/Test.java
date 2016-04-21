import java.util.List;

public class Test {
    public static void main(String[] args) {
        TraceGenerator generator = new TraceGenerator(4,4,4,4,8,12,4,4,3,5);
        Computation computation = generator.generateTrace();
        System.out.println(computation);
        App app = new App(false, true, false, false);
        List<Computation> computations = app.generateComputations(computation);
        System.out.println(computations.get(0));
    }
}
