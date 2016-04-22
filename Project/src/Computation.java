import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that represents a computation
 */
public class Computation {
    private Map<Integer, Event> events;
    private Map<Integer, Set<Integer>> messages; // also includes edges on single processor
    private Map<Integer, Set<Integer>> syncMessages;
    private Map<Integer, List<Integer>> processesEvents;
    private Map<Integer, Set<Integer>> reachableEvents;

    public Computation() {
        events = new HashMap<>();
        messages = new HashMap<>();
        syncMessages = new HashMap<>();
        processesEvents = new HashMap<>();
        reachableEvents = new HashMap<>();
    }

    public Computation(Computation otherComputation) {
        events = new HashMap<>(otherComputation.events);
        messages = new HashMap<>(otherComputation.messages);
        syncMessages = new HashMap<>(otherComputation.syncMessages);
        processesEvents = new HashMap<>(otherComputation.processesEvents);
        reachableEvents = new HashMap<>();
    }

    /**
     * Adds a event to the computation. Also adds edge to this event from the previous event
     * on the same process (if this is not the initial event)
     * Should be called on increasing order of event id per process
     * @param processId ID of the process on which this event has run
     * @param eventId ID of the event
     */
    public void addEvent(int processId, int eventId) {
        Event event = new Event(processId, eventId);

        // first add it to Process <--> Event map and check to see who the prev event is
        if (processesEvents.get(processId) == null) {
            processesEvents.put(processId, new ArrayList<Integer>());
        }
        List<Integer> thisProcessEvents = processesEvents.get(processId);
        int prevEventId = -1;
        if (!thisProcessEvents.isEmpty()) {
            prevEventId = thisProcessEvents.get(thisProcessEvents.size()-1);
        }
        thisProcessEvents.add(eventId);

        // add it to list of events
        events.put(eventId, event);

        // add edge from the previous event on this process
        if (prevEventId != -1) {
            if (messages.get(prevEventId) == null) {
                messages.put(prevEventId, new HashSet<Integer>());
            }
            messages.get(prevEventId).add(eventId);
        }
    }

    /**
     * Adds a message between two events. No need to call this for consecutive events
     * on the same process as this is automatically taken care of in addEvent
     * Precondition: addEvent should be called on both e1 and e2
     * @param e1 start node of edge
     * @param e2 end node of edge
     */
    public boolean addMessage(int e1, int e2) {
        if (!repOk(e1, e2)) {
            return false;
        }

        if (messages.get(e1) == null) {
            messages.put(e1, new HashSet<Integer>());
        }
        messages.get(e1).add(e2);

        return true;
    }

    /**
     * Adds a sync message between two events. The function will call addMessage
     * Precondition: addEvent should be called on both e1 and e2
     * @param e1 start node of edge
     * @param e2 end node of edge
     */
    public boolean addSyncMessage(int e1, int e2) {
        if (!addMessage(e1, e2)) {
            return false;
        }
        if (syncMessages.get(e1) == null) {
            syncMessages.put(e1, new HashSet<Integer>());
        }
        syncMessages.get(e1).add(e2);
        return true;
    }

    public int getNumberOfProcesses() {
        return processesEvents.size();
    }

    public Map<Integer, Set<Integer>> getMessages() {
        return messages;
    }

    public Map<Integer, Set<Integer>> getSyncMessages() {
        return syncMessages;
    }

    public int getInitialProcessEventId(int processId) {
        List<Integer> thisProcessEvents = processesEvents.get(processId);

        if (thisProcessEvents.isEmpty()) {
            return -1;
        }
        return thisProcessEvents.get(0);
    }

    public int getFinalProcessEventId(int processId) {
        List<Integer> thisProcessEvents = processesEvents.get(processId);

        if (thisProcessEvents.isEmpty()) {
            return -1;
        }
        return thisProcessEvents.get(thisProcessEvents.size() - 1);
    }

    public Event getEventById(int eventId) {
        return events.get(eventId);
    }

    @Override
    public String toString() {
        /*
        Format:
        A line for every process of the form
        <pid>:<nbr of events>
        Followed by a line containing the following terminal: READ
        Followed by: a line for every event that has a read variable of the form
        <pid>:<readVar1>,<readVar2>...
        Followed by a line containing the following terminal: WRITE
        Followed by: a line for every event that has a write variable of the form
        <pid>:<writeVar1>,<writeVar2>...
        Followed by a line containing the following terminal: MESSAGES
        Followed by a line for every event that is a source of message(s) (not including same-process msgs)
        <evtID>:<destEvtID1>,<destEvtID2>...
         */
        String output = super.toString() + "\n";
        for (Map.Entry<Integer,List<Integer>> eventForProcess : processesEvents.entrySet()) {
            int pid = eventForProcess.getKey();
            int size = eventForProcess.getValue().size();
            output += pid + ":" + size + "\n";
        }
        output += "READ\n";
        for (Map.Entry<Integer,Event> eventEntry : events.entrySet()) {
            int evtId = eventEntry.getKey();
            Set<String> readVariables = eventEntry.getValue().getReadVariables();
            if (!readVariables.isEmpty()) {
                output += evtId + ":" + Utils.joinCollection(readVariables, ",") + "\n";
            }
        }
        output += "WRITE\n";
        for (Map.Entry<Integer,Event> eventEntry : events.entrySet()) {
            int evtId = eventEntry.getKey();
            Set<String> writeVariables = eventEntry.getValue().getWriteVariables();
            if (!writeVariables.isEmpty()) {
                output += evtId + ":" + Utils.joinCollection(writeVariables, ",") + "\n";
            }
        }
        output += "MESSAGES\n";
        for (Map.Entry<Integer,Set<Integer>> msgEntry : messages.entrySet()) {
            Event sourceEvt = events.get(msgEntry.getKey());
            int sourceProcessId = sourceEvt.getProcessId();

            String outgoingMessages = "";
            String delimeter = "";

            for (int destEvtId : msgEntry.getValue()) {
                int destProcessId = events.get(destEvtId).getProcessId();
                if (sourceProcessId != destProcessId) {
                    outgoingMessages += delimeter + destEvtId;
                    delimeter = ",";
                }
            }

            if (!outgoingMessages.equals("")) {
                output += sourceEvt.getEventId() + ":" + outgoingMessages + "\n";
            }
        }

        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Computation)) {
            return false;
        }

        Computation other = (Computation)o;

        return events.equals(other.events) && messages.equals(other.messages)
                && processesEvents.equals(other.processesEvents);
    }

    /**
     * Merge this computation with otherComputation by creating a new computation
     * that has the messages in both
     * Precondition: otherComputation must have the same events and processes as this one
     * @param otherComputation the other computation
     * @return the merged computation
     */
    public Computation mergeWith(Computation otherComputation) {
        Computation result = new Computation(this);

        for (Map.Entry<Integer, Set<Integer>> otherEntry : otherComputation.messages.entrySet()) {
            int fromId = otherEntry.getKey();
            for (int toId : otherEntry.getValue()) {
                if (otherComputation.syncMessages.get(fromId) != null && otherComputation.syncMessages.get(fromId).contains(toId)) {
                    result.addSyncMessage(fromId, toId);
                } else {
                    result.addMessage(fromId, toId);
                }
            }
        }

        return result;
    }

    /**
     * Gets the concurrent events in this computation
     * @return a map between each event and a list of all other events it is concurrent with
     */
    public Map<Integer, List<Integer>> getConcurrentEvents() {
        this.reachableEvents.clear();
        Map<Integer, List<Integer>> results = new HashMap<>();

        for (Map.Entry<Integer, Event> entry : this.events.entrySet()) {
            int fromID = entry.getKey();
            List<Integer> concurrentEvents = new ArrayList<>(this.events.keySet());
            Set<Integer> reachableEvents = getReachableEvents(fromID);

            concurrentEvents.removeAll(reachableEvents);
            concurrentEvents = concurrentEvents.stream().filter(e -> !isReachable(e, fromID))
                    .collect(Collectors.toList());
            concurrentEvents.remove(new Integer(fromID));

            results.put(fromID, concurrentEvents);
        }

        return results;
    }

    /**
     * Performs topological sort on the events in this computation
     * @return a list where each entry is a set of events that happen before
     * all the events in the subsequent elements
     */
    public List<Set<Integer>> topologicalSort() {
        // compute inverse map of messages
        Map<Integer, Set<Integer>> incomingEdges = new HashMap<>();
        for (int evtId : events.keySet()) {
            incomingEdges.put(evtId, new HashSet<>());
        }
        for (Map.Entry<Integer, Set<Integer>> entry : messages.entrySet()) {
            int fromId = entry.getKey();
            for (int toId : entry.getValue()) {
                incomingEdges.get(toId).add(fromId);
            }
        }

        List<Set<Integer>> sortedList = new ArrayList<>();
        while (!incomingEdges.isEmpty()) {
            sortedList.add(topologicalSortHelper(incomingEdges));
        }
        return sortedList;
    }

    public Set<Integer> topologicalSortHelper(Map<Integer, Set<Integer>> incomingEdges) {
        // get all events with no incoming edge
        Map<Integer,Set<Integer>> firstLevelEntries = incomingEdges.entrySet().stream()
                .filter(e -> e.getValue() == null || e.getValue().isEmpty())
                .collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
        Set<Integer> firstLevelEvents = firstLevelEntries.keySet();

        Map<Integer, Set<Integer>> remainingEntries = incomingEdges.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .collect(Collectors.toMap(e -> {
                    return e.getKey();
                }, e -> {
                    Set<Integer> initialSet = e.getValue();
                    initialSet.removeAll(firstLevelEvents);
                    return initialSet;
                }));

        incomingEdges.clear();
        incomingEdges.putAll(remainingEntries);

        return firstLevelEvents;
    }

    private Set<Integer> getReachableEvents(int fromId) {
        if (this.reachableEvents.containsKey(fromId)) {
            Set<Integer> s = this.reachableEvents.get(fromId);
            return s;
        }

        Set<Integer> reachableEvents = new HashSet<>();
        if (this.messages.get(fromId) == null) {
            return reachableEvents;
        }

        for (Integer toId : this.messages.get(fromId)) {
            reachableEvents.addAll(getReachableEvents(toId));
        }

        this.reachableEvents.put(fromId, reachableEvents);
        return reachableEvents;
    }
    
    /**
     * Checks whether adding a message to the computation would results in a cycle
     * Precondition: the computation must not have a cycle
     * @param e1 the start node of the edge to be added
     * @param e2 the end node of the edge to be added
     * @return true if the computation with the added (e1, e2) message has no cycles, false otherwise
     */
    private boolean repOk(int e1, int e2) {
        // To check if there's a cycle after adding (e1, e2), we can check if
        // e1 is reachable from e2
        return !isReachable(e2, e1);
    }

    private boolean isReachable(int eventFrom, int eventTo) {
        Set<Integer> outgoingEvents = messages.get(eventFrom);
        if (outgoingEvents == null) {
            return false;
        }

        for (Integer intermediateEvent : outgoingEvents) {
            if (intermediateEvent.equals(eventTo)) {
                return true;
            }
            if (isReachable(intermediateEvent, eventTo)) {
                return true;
            }
        }

        return false;
    }

    public static Computation parseComputation(String input) {
        Computation computation = new Computation();
        String[] lines = input.split("\n");

        int i = 0;
        int evtId = 0;
        for (i = 0; i < lines.length; i++) {
            if (lines[i].equals("READ")) { break; }
            String[] fields = lines[i].split(":");
            int pid = Integer.parseInt(fields[0]);
            int nbrOfEvents = Integer.parseInt(fields[1]);
            for (int j = 0; j < nbrOfEvents; j++) {
                computation.addEvent(pid, evtId);
                evtId++;
            }
        }

        i++;
        for (; i < lines.length; i++) {
            if (lines[i].equals("WRITE")) { break; }
            String[] fields = lines[i].split(":");
            String[] vars = fields[1].split(",");
            evtId = Integer.parseInt(fields[0]);
            Event evt = computation.getEventById(evtId);
            for (String var : vars) {
                evt.addReadVariable(var);
            }
        }

        i++;
        for (; i < lines.length; i++) {
            if (lines[i].equals("MESSAGES")) { break; }
            String[] fields = lines[i].split(":");
            String[] vars = fields[1].split(",");
            evtId = Integer.parseInt(fields[0]);
            Event evt = computation.getEventById(evtId);
            for (String var : vars) {
                evt.addWriteVariable(var);
            }
        }

        i++;
        for (; i < lines.length; i++) {
            String[] fields = lines[i].split(":");
            String[] destinations = fields[1].split(",");
            int sourceId = Integer.parseInt(fields[0]);
            for (String dest : destinations) {
                int destId = Integer.parseInt(dest);
                computation.addMessage(sourceId, destId);
            }
        }

        return computation;
    }

    /**
     * For debugging
     */
    public boolean hasCycle() {
        for (Map.Entry<Integer, Event> entry : events.entrySet()) {
            Set<Integer> ancestors = new HashSet<>();
            if (hasBackEdge(entry.getKey(), ancestors)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasBackEdge(int source,Set<Integer> ancestors) {
        if (ancestors.contains(source)) {
            return true;
        }
        ancestors.add(source);
        if (messages.get(source) == null) {
            ancestors.remove(new Integer(source));
            return false;
        }
        for (int dest : messages.get(source)) {
            if (hasBackEdge(dest, ancestors)) {
                return true;
            }
        }
        ancestors.remove(new Integer(source));
        return false;
    }
}
