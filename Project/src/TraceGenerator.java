/**
 * This class generates random computation traces according to
 * some parameters.
 */
public class TraceGenerator {
    int minNbrProcessors;
    int maxNbrProcessors;
    int minEventsPerProcess;
    int maxEventsPerProcess;
    int minNbrMsgs;
    int maxNbrMsgs;
    int minNbrOfVariables;
    int maxNbrOfVariables;
    int minRepeats;
    int maxRepeats;

    public TraceGenerator(int minNbrProcessors,
                          int maxNbrProcessors,
                          int minEventsPerProcess,
                          int maxEventsPerProcess,
                          int minNbrMsgs,
                          int maxNbrMsgs,
                          int minNbrOfVariables,
                          int maxNbrOfVariables,
                          int minRepeats,
                          int maxRepeats) {
        this.minNbrProcessors = minNbrProcessors;
        this.maxNbrProcessors = maxNbrProcessors;
        this.minEventsPerProcess = minEventsPerProcess;
        this.maxEventsPerProcess = maxEventsPerProcess;
        this.minNbrMsgs = minNbrMsgs;
        this.maxNbrMsgs = maxNbrMsgs;
        this.minNbrOfVariables = minNbrOfVariables;
        this.maxNbrOfVariables = maxNbrOfVariables;
        this.minRepeats = minRepeats;
        this.maxRepeats = maxRepeats;
    }

    /**
     * Generates random computation according to the params of this class
     * @return the generated computation
     */
    public Computation generateTrace() {
        Computation computation = new Computation();

        addEvents(computation);
        addMessages(computation);
        addVariables(computation);

        return computation;
    }

    /**
     * Adds events according to the params to the computation
     * @param computation the computation to be populated
     */
    private void addEvents(Computation computation) {
        Logger.log("Adding event to computation");

        int nbrOfProcesses = generateRandomVariable(minNbrProcessors, maxNbrProcessors);

        int eventId = 0;
        int processId = 0;

        for (processId = 0; processId < nbrOfProcesses; processId++) {
            int nbrOfEvents = generateRandomVariable(minEventsPerProcess, maxEventsPerProcess);

            for (int eventIndex = 0; eventIndex < nbrOfEvents; eventIndex++) {
                computation.addEvent(processId, eventId);
                eventId++;
            }
        }
    }

    /**
     * Adds messages according to the params to the computation
     * @param computation the computation to be populated
     */
    private void addMessages(Computation computation) {
        Logger.log("Adding messages to computation");

        int nbrOfProcesses = computation.getNumberOfProcesses();

        // add messages
        int nbrOfMessages = generateRandomVariable(minNbrMsgs, maxNbrMsgs);
        int messagesAdded = 0;

        while (messagesAdded < nbrOfMessages) {
            // choose different start and end processes
            int sender = generateRandomVariable(0, nbrOfProcesses-1);
            int receiver = generateRandomVariable(0, nbrOfProcesses-1);
            if (sender == receiver) {
                continue;
            }

            // choose start and end events
            int sendEvent = generateRandomVariable(computation.getInitialProcessEventId(sender),
                    computation.getFinalProcessEventId(sender));
            int receiveEvent = generateRandomVariable(computation.getInitialProcessEventId(receiver),
                    computation.getFinalProcessEventId(receiver));

            // try to add the message
            if (computation.addMessage(sendEvent, receiveEvent)) {
                messagesAdded++;
            }
        }
    }

    /**
     * Adds shared variables according to the params to the computation
     * @param computation the computation to be populated
     */
    private void addVariables(Computation computation) {
        Logger.log("Adding variables to computation");

        int nbrOfProcesses = computation.getNumberOfProcesses();

        int nbrOfVariables = generateRandomVariable(minNbrOfVariables, maxNbrMsgs);

        for (int varId = 0; varId < nbrOfVariables; varId++) {
            String variable = "x" + varId;
            int repeats = generateRandomVariable(minRepeats, maxRepeats);

            for (int r = 0; r < repeats; r++) {
                // choose a process
                int processId = generateRandomVariable(0, nbrOfProcesses - 1);

                // choose an event
                int eventId = generateRandomVariable(computation.getInitialProcessEventId(processId),
                        computation.getFinalProcessEventId(processId));
                Event event = computation.getEventById(eventId);

                // choose an access mode
                boolean read = (Math.random() < 0.5) ? true : false;
                if (read) {
                    event.addReadVariable(variable);
                } else {
                    event.addWriteVariable(variable);
                }
            }
        }
    }

    /**
     * Generates a random variable within a certain range uniformly
     * @param min lower threshold of range
     * @param max upper threshold of range
     * @return random numebr in [min, max]
     */
    private int generateRandomVariable(int min, int max) {
        return min + (int)((max-min)*Math.random());
    }
}
