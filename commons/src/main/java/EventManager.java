import java.util.ArrayList;
import java.util.List;

/**
 * The EventManager class is responsible for managing events and
 * their participants in the server.
 */
public class EventManager {
    private List<Event> events;

    /**
     * Constructor for the EventManager class.
     */
    public EventManager() {
        this.events = new ArrayList<>();
    }

    /**
     * Constructor for the EventManager class.
     *
     * @param events the list of events to be managed.
     */
    public EventManager(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void removeEvent(Event event) {
        this.events.remove(event);
    }

    public List<Event> getEvents() {
        return this.events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}