package commons;

import java.util.ArrayList;
import java.util.List;
import commons.transactions.Transaction;

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


    /**
     * Returns a list of events that a participant is part of.
     *
     * @param participant the participant to search for.
     * @return a list of events that the participant is part of.
     */
    public List<Event> getEventsByParticipant(User participant) {
        List<Event> participantEvents = new ArrayList<>();
        for (Event event : this.events) {
            if (event.getParticipants().containsKey(participant)) {
                participantEvents.add(event);
            }
        }
        return participantEvents;
    }

    /**
     * Returns a list of events with a given title.
     *
     * @param title the title to search for.
     * @return a list of events with the given title.
     */
    public List<Event> getEventsByTitle(String title) {
        List<Event> titleEvents = new ArrayList<>();
        for (Event event : this.events) {
            if (event.getTitle().equals(title)) {
                titleEvents.add(event);
            }
        }
        return titleEvents;
    }

    /**
     * Returns an event with a given transaction or throws an exception if not found.
     *
     * @param transaction the transaction to search for.
     * @return the event with the given transaction.
     * @throws IllegalArgumentException if the transaction is not found in any event.
     */
    public Event getEventByTransaction(Transaction transaction) throws IllegalArgumentException {
        Event transactionEvent = null;
        for (Event event : this.events) {
            if (event.getTransactions().contains(transaction)) {
                transactionEvent = event;
                break;
            }
        }

        if (transactionEvent == null) {
            throw new IllegalArgumentException("Transaction not found in any event");
        }

        return transactionEvent;
    }

    /**
     * Returns an event with a given invite code or throws an exception if not found.
     *
     * @param inviteCode the invite code to search for.
     * @return the event with the given invite code.
     * @throws IllegalArgumentException if the event is not found with the given invite code.
     */
    public Event getEventByInviteCode(String inviteCode) throws IllegalArgumentException {
        Event inviteEvent = null;
        for (Event event : this.events) {
            if (event.getInviteCode().equals(inviteCode)) {
                inviteEvent = event;
                break;
            }
        }

        if (inviteEvent == null) {
            throw new IllegalArgumentException("Event not found with the given invite code");
        }

        return inviteEvent;
    }

}