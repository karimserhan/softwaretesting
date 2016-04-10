import java.util.*;
/**
 * Class that represents a computation
 */
public class Computation {
    Set<Event> eventList;
    Map<Event, Set<Event>> messages; // also includes edges on single processor
    Map<Integer, List<Event>> processesEvents;

    public Computation() {
        eventList = new HashSet<>();
        messages = new HashMap<>();
        processesEvents = new HashMap<>();
    }

    /**
     * Adds a node to the computation. Also adds to this node from the previous event
     * on the same process (if this is not the initial event)
     * Should be called on increasing order of event id per process
     * @param processId ID of the process on which this event has run
     * @param eventId ID of the event
     */
    public void addEvent(int processId, int eventId) {
        Event event = new Event(processId, eventId);

        // first add it to Process <--> Event map and check to see who the prev event is
        if (processesEvents.get(processId) == null) {
            processesEvents.put(processId, new ArrayList<Event>());
        }
        List<Event> thisProcessEvents = processesEvents.get(processId);
        Event prevEvent = null;
        if (!thisProcessEvents.isEmpty()) {
            prevEvent = thisProcessEvents.get(thisProcessEvents.size()-1);
        }
        thisProcessEvents.add(event);

        // add it to list of events
        eventList.add(event);

        // add edge from the previous event on this process
        if (prevEvent != null) {
            if (messages.get(event) == null) {
                messages.put(event, new HashSet<Event>());
            }
            messages.get(event).add(prevEvent);
        }
    }

    /**
     * Adds a message between two event. No need to call this for consecutive events
     * on the same process as this is automatically taken care of in addEvent
     * Precondition: addEvent should be called on both e1 and e2
     * @param e1 start node of edge
     * @param e2 end node of edge
     */
    public void addMessage(Event e1, Event e2) {
        if (messages.get(e1) == null) {
            messages.put(e1, new HashSet<Event>());
        }
        messages.get(e1).add(e2);
    }
}
