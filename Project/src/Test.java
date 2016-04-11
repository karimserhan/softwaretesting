public class Test {
    public static void main(String[] args) {
        TraceGenerator generator = new TraceGenerator(4,4,4,4,8,12,4,4);
        Logger.log("Generating computation");
        Computation computation = generator.generateTrace();

        // verify parsing and outputing is correct
        Computation computation2 = Computation.parseComputation(computation.toString());
        Logger.log("Parsing: " + computation.equals(computation2));
        Logger.log("Outputting: " + computation.toString().equals(computation2.toString()));

    }
}
