import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that represents an event in a computation
 */
public class Event implements Comparable<Event> {
    private int eventId;
    private int processId;
    private Set<String> readVariables;
    private Set<String> writeVariables;

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
    public int compareTo(Event o) {
        if (o == null) { throw new NullPointerException(); }
        Integer me = new Integer(this.getEventId());
        Integer other = new Integer(o.getEventId());
        return me.compareTo(other);
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
