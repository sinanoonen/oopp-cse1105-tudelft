package server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import commons.Event;
import commons.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Transaction;
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
        exRepo = new TestExpenseRepository();
        payRepo = new TestPaymentRepository();
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

    @Test
    public void getEventByUuidWhenEventExists() {
        Event testEvent = getEvent("Test Event");
        sut.add(testEvent);
        UUID uuid = testEvent.getInviteCode();

        ResponseEntity<Event> response = sut.getById(uuid);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testEvent, response.getBody());
    }

    @Test
    public void getEventByUuidWhenEventDoesNotExist() {
        UUID testUuid = UUID.randomUUID();

        ResponseEntity<Event> response = sut.getById(testUuid);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addUserToExistingEvent() {
        Event testEvent = getEvent("Existing Event");
        User newUser = new User("newName", "newMail", "newIBAN", "newBic");

        sut.add(testEvent);
        UUID testUuid = testEvent.getInviteCode();

        ResponseEntity<Event> response = sut.addUser(testUuid, newUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(testEvent.getParticipants().contains(newUser));
    }

    @Test
    public void addUserToNonExistingEvent() {
        UUID testUuid = UUID.randomUUID();
        User newUser = new User("newName", "newMail", "newIBAN", "newBic");

        ResponseEntity<Event> response = sut.addUser(testUuid, newUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void removeUserFromEvent() {
        User existingUser = new User("existingName", "existingMail", "existingIBAN", "existingBic");
        Event testEvent = getEvent("Event");
        UUID testUuid = testEvent.getInviteCode();
        testEvent.addParticipant(existingUser);

        repo.save(testEvent);

        ResponseEntity<Event> response = sut.removeUserFromEvent(testUuid, existingUser.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(testEvent.getParticipants().contains(existingUser));
    }

    @Test
    public void removeUserFromNonExistingEvent() {
        UUID testUuid = UUID.randomUUID();

        ResponseEntity<Event> response = sut.removeUserFromEvent(testUuid, "nonexistent@mail.com");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void removeNullFromExistingEvent() {
        Event event = getEvent("Test Event");
        UUID testUuid = event.getInviteCode();

        ResponseEntity<Event> response = sut.removeUserFromEvent(testUuid, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void removeNonExistingUserFromEvent() {
        Event event = getEvent("Test Event");
        UUID testUuid = event.getInviteCode();
        sut.add(event);

        ResponseEntity<Event> response = sut.removeUserFromEvent(testUuid, "doesnt@exist.com");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAllTransactionsForEventWhenEventExists() {

        Event testEvent = getEvent("Test Event");
        LocalDate baseDate = LocalDate.of(2024, 3, 1);
        Transaction transaction1 = new Expense("TestExpense", baseDate, 90f,
            "Just a test", new ArrayList<>());
        Transaction transaction2 = new Payment(baseDate, 100f, "Person1", "Person2");

        testEvent.addTransaction(transaction1);
        testEvent.addTransaction(transaction2);
        UUID testUuid = testEvent.getInviteCode();
        repo.save(testEvent);

        ResponseEntity<List<Transaction>> response = sut.getAllTransactionsForEvent(testUuid);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void getAllTransactionsForNonExistingEvent() {
        UUID testUuid = UUID.randomUUID();

        ResponseEntity<List<Transaction>> response = sut.getAllTransactionsForEvent(testUuid);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void getTransactionByIdForEventWhenTransactionExists() {
        Event testEvent = getEvent("Test Event");
        LocalDate baseDate = LocalDate.of(2024, 3, 1);
        Transaction transaction = new Expense("TestExpense", baseDate, 90f,
            "Just a test", new ArrayList<>());
        testEvent.addTransaction(transaction);

        repo.save(testEvent);
        UUID testUuid = testEvent.getInviteCode();

        ResponseEntity<Transaction> response = sut.getTransactionByIdforEvent(testUuid, transaction.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaction, response.getBody());
    }

    @Test
    public void getTransactionByIdForEventWhenTransactionDoesNotExist() {
        Event testEvent = getEvent("Test Event");
        repo.save(testEvent);
        UUID testUuid = testEvent.getInviteCode();
        ResponseEntity<Transaction> response = sut.getTransactionByIdforEvent(testUuid, 0L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addExpenseToEventSuccessfully() {
        Event testEvent = getEvent("Test Event");
        LocalDate baseDate = LocalDate.of(2024, 3, 1);
        Expense expense = new Expense("TestExpense", baseDate, 90f,
            "Just a test", new ArrayList<>());

        repo.save(testEvent);
        UUID testUuid = testEvent.getInviteCode();
        ResponseEntity<Expense> response = sut.addExpenseToEvent(testUuid, expense);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(exRepo.calledMethods.contains("save"));
        assertEquals(expense, exRepo.getById(expense.getId()));
    }

    @Test
    public void addExpenseToNonExistingEvent() {
        UUID testUuid = UUID.randomUUID();
        LocalDate baseDate = LocalDate.of(2024, 3, 1);
        Expense expense = new Expense("TestExpense", baseDate, 90f,
            "Just a test", new ArrayList<>());

        ResponseEntity<Expense> response = sut.addExpenseToEvent(testUuid, expense);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addPaymentToEventSuccessfully() {
        Event testEvent = getEvent("Test Event");
        LocalDate baseDate = LocalDate.of(2024, 3, 1);
        Payment payment = new Payment(baseDate, 100f, "Person1", "Person2");
        UUID testUuid = testEvent.getInviteCode();
        repo.save(testEvent);

        ResponseEntity<Payment> response = sut.addPaymentToEvent(testUuid, payment);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(payRepo.calledMethods.contains("save"));
        assertEquals(payment, payRepo.getById(payment.getId()));
    }

    @Test
    public void addPaymentToNonExistingEvent() {
        UUID testUuid = UUID.randomUUID();
        LocalDate baseDate = LocalDate.of(2024, 3, 1);
        Payment payment = new Payment(baseDate, 100f, "Person1", "Person2");

        ResponseEntity<Payment> response = sut.addPaymentToEvent(testUuid, payment);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}