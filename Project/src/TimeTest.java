import java.util.List;

public class TimeTest {
    private static int NUM_RUNS = 100;

    private static void time(int minNbrProcessors,
                             int maxNbrProcessors,
                             int minEventsPerProcess,
                             int maxEventsPerProcess,
                             int minNbrMsgs,
                             int maxNbrMsgs,
                             int minNbrOfVariables,
                             int maxNbrOfVariables,
                             int minRepeats,
                             int maxRepeats) {
        App app = new App(false, true, true, true);
        long[] times = new long[NUM_RUNS];
        long[] sizes = new long[NUM_RUNS];

        for (int i = 0; i < NUM_RUNS; i += 1) {
            System.out.println("Running iteration #" + i + ":");
            Computation comp = new TraceGenerator(minNbrProcessors,
                    maxNbrProcessors,
                    minEventsPerProcess,
                    maxEventsPerProcess,
                    minNbrMsgs,
                    maxNbrMsgs,
                    minNbrOfVariables,
                    maxNbrOfVariables,
                    minRepeats,
                    maxRepeats).generateTrace();
            long start = System.nanoTime();
            List<Computation> comps = app.generateComputations(comp);
            long elapsed = System.nanoTime() - start;

            times[i] = elapsed;
            sizes[i] = comps.size();
            System.out.println("Took: " + times[i] + " nanoseconds");
            System.out.println("Output list size: " + sizes[i]);
        }

        long timesSum = 0;
        long sizesSum = 0;

        for (int i = 0; i < NUM_RUNS; i += 1) {
            timesSum += times[i];
            sizesSum += sizes[i];
        }

        System.out.println("Average time (ms): " + timesSum/(NUM_RUNS*(10e3)));
        System.out.println("Average list size: "+ sizesSum/NUM_RUNS);
    }

    public static void main(String[] args) {
        time(3,3,5,5,7,7, 5,5,5,5);
    }
}
