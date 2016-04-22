import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents an event in a computation
 */
public class Event {
    private int eventId;
    private int processId;
    private Set<String> readVariables;
    private Set<String> writeVariables;

    public Event(Event evt) {
        this(evt.getProcessId(), evt.getEventId());
        this.readVariables = new HashSet<>(evt.readVariables);
        this.writeVariables = new HashSet<>(evt.writeVariables);
    }

    public Event(int processId, int eventId) {
        this.eventId = eventId;
        this.processId = processId;
        this.readVariables = new HashSet<>();
        this.writeVariables = new HashSet<>();
    }

    public int getEventId() {
        return eventId;
    }

    public int getProcessId() {
        return processId;
    }

    public Set<String> getReadVariables() {
        return readVariables;
    }

    public Set<String> getWriteVariables() {
        return writeVariables;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public void addReadVariable(String variableName) {
        readVariables.add(variableName);
    }

    public void addWriteVariable(String variableName) {
        writeVariables.add(variableName);
    }

    @Override
    public String toString() {
        return new Integer(eventId).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Event) {
            return this.getEventId() == ((Event)o).getEventId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.eventId;
    }
}
