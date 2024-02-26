import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transactions.Expense;
import transactions.Payment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventManagerTest {

    EventManager eventManagerEmpty;
    EventManager eventManagerFull;
    Event testEvent;
    Event testEvent2;
    Expense testExpense;
    Payment testPayment;

    @BeforeEach
    void setUp() {
        eventManagerEmpty = new EventManager();

        testEvent = new Event("title", new User("name", "email", "iban", "bic"));
        List<Event> events = new ArrayList<>();
        events.add(testEvent);

        testEvent2 = new Event("title2", new User("name2", "email2", "iban2", "bic2"));

        testExpense = new Expense("emilio", LocalDate.of(2024,1,1), 100, "description", List.of("emilio", "mario"));

        testPayment = new Payment("emilio", LocalDate.of(2024,1,1), 100,"emilio", "mario");

        eventManagerFull = new EventManager(events);
    }

    @Test
    void constructor() {
        assertNotNull(eventManagerEmpty);
        assertNotNull(eventManagerFull);
        assertEquals(new ArrayList<>(), eventManagerEmpty.getEvents());
        assertEquals(List.of(testEvent), eventManagerFull.getEvents());
    }

    @Test
    void addEvent() {
        eventManagerEmpty.addEvent(testEvent);
        assertEquals(List.of(testEvent), eventManagerEmpty.getEvents());

        eventManagerFull.addEvent(testEvent2);
        assertEquals(List.of(testEvent, testEvent2), eventManagerFull.getEvents());
    }

    @Test
    void removeEvent() {
        eventManagerFull.addEvent(testEvent2);

        eventManagerFull.removeEvent(testEvent);
        assertEquals(List.of(testEvent2), eventManagerFull.getEvents());

        assertThrows(IllegalArgumentException.class, () -> eventManagerEmpty.removeEvent(testEvent));
    }

    @Test
    void getEvents() {
        assertEquals(new ArrayList<>(), eventManagerEmpty.getEvents());
        assertEquals(List.of(testEvent), eventManagerFull.getEvents());
    }

    @Test
    void setEvents() {
        List<Event> events = new ArrayList<>();
        events.add(testEvent2);

        eventManagerEmpty.setEvents(events);
        assertEquals(events, eventManagerEmpty.getEvents());

        eventManagerFull.setEvents(events);
        assertEquals(events, eventManagerFull.getEvents());
    }

    @Test
    void getEventsByParticipant() {
        User user = new User("name", "email", "iban", "bic");
        User user2 = new User("name2", "email2", "iban2", "bic2");

        testEvent.addParticipant(user);
        testEvent2.addParticipant(user2);
        eventManagerFull.addEvent(testEvent2);

        assertEquals(List.of(testEvent), eventManagerFull.getEventsByParticipant(user));
        assertEquals(List.of(testEvent2), eventManagerFull.getEventsByParticipant(user2));

        assertEquals(new ArrayList<>(), eventManagerEmpty.getEventsByParticipant(user));
        assertEquals(new ArrayList<>(), eventManagerEmpty.getEventsByParticipant(user2));
    }

    @Test
    void getEventsByTitle() {
        eventManagerFull.addEvent(testEvent2);

        assertEquals(List.of(testEvent), eventManagerFull.getEventsByTitle("title"));
        assertEquals(List.of(testEvent2), eventManagerFull.getEventsByTitle("title2"));
        assertEquals(new ArrayList<>(), eventManagerEmpty.getEventsByTitle("title1"));
        assertEquals(new ArrayList<>(), eventManagerEmpty.getEventsByTitle("title2"));
    }

    @Test
    void getEventByTransaction() {
        testEvent.addTransaction(testExpense);
        testEvent2.addTransaction(testPayment);

        eventManagerFull.addEvent(testEvent2);
        assertEquals(testEvent, eventManagerFull.getEventByTransaction(testExpense));
        assertEquals(testEvent2, eventManagerFull.getEventByTransaction(testPayment));

        assertThrows(IllegalArgumentException.class, () -> eventManagerEmpty.getEventByTransaction(testExpense));
        assertThrows(IllegalArgumentException.class, () -> eventManagerEmpty.getEventByTransaction(testPayment));
    }

    @Test
    void getEventByInviteCode() {
        testEvent.setInviteCode("1234");
        testEvent2.setInviteCode("5678");

        eventManagerFull.addEvent(testEvent2);
        assertEquals(testEvent, eventManagerFull.getEventByInviteCode("1234"));
        assertEquals(testEvent2, eventManagerFull.getEventByInviteCode("5678"));

        assertThrows(IllegalArgumentException.class, () -> eventManagerEmpty.getEventByInviteCode("1234"));
        assertThrows(IllegalArgumentException.class, () -> eventManagerEmpty.getEventByInviteCode("5678"));
    }
}