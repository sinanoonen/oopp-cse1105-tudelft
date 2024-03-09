package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import commons.Event;
import commons.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Tests for Event Controller.
 */
public class EventControllerTest {

    private TestEventRepository repo;
    private EventController sut;
    private TestExpenseRepository exRepo;
    private TestPaymentRepository payRepo;

    /**
     * A test event repository.
     */
    @BeforeEach
    public void setup() {
        repo = new TestEventRepository();
        sut = new EventController(repo, exRepo, payRepo);
    }

    @Test
    public void cannotAddNullEvent() {
        var actual = sut.add(getEvent(null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsed() {
        sut.add(getEvent("test"));
        repo.calledMethods.contains("save");
    }

    private static Event getEvent(String title) {
        Event event = new Event(title);
        event.addParticipant(new User("testName", "testMail", "testIBAN", "testBic"));
        return event;
    }

    @Test
    public void addValidEvent() {
        Event event = getEvent("New Event");
        ResponseEntity<Event> response = sut.add(event);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(repo.calledMethods.contains("save"));
        assertTrue(repo.events.contains(event));
    }

    @Test
    public void getAllEvents() {
        List<Event> list = new ArrayList<>();

        Event event1 = getEvent("Event1");
        Event event2 = getEvent("Event2");

        list.add(event1);
        list.add(event2);

        sut.add(event1);
        sut.add(event2);

        List<Event> response = sut.getAll();

        assertEquals(response, list);
        assertTrue(repo.calledMethods.contains("findAll"));
    }


}