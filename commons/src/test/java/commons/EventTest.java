package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Tag;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test for the Event class.
 */
public class EventTest {

    private Event event;
    private Set<Tag> newTags;
    private Set<User> newParticipants;
    private List<Expense> newExpenses;
    private List<Payment> newPayments;

    /**
     * This is the setup for testing.
     */
    @BeforeEach
    public void setup() {
        event = new Event("Test Event");

        newTags = new HashSet<>();
        newTags.add(new Tag("NewTag1", new Color(120, 120, 120)));

        User user1 = new User("Alice", "alice@gmail.com", "NL11BANK0123456789", "BICCODE1", UUID.randomUUID());
        newParticipants = new HashSet<>();
        newParticipants.add(user1);

        newExpenses = List.of(
            new Expense("Alice", LocalDate.now(), 10.0f, Currency.EUR, "Test Expense 1", List.of("Alice"))
        );

        newPayments = List.of(
            new Payment(LocalDate.now(), 20.0f, Currency.EUR, "Alice", "Bob")
        );
    }

    @Test
    public void testCreationAndLastActivityDates() {
        LocalDateTime creationDateBeforeSet = event.getCreationDate();
        LocalDateTime lastActivityBeforeSet = event.getLastActivity();
        LocalDateTime newDate = LocalDateTime.of(2023, 12, 1, 1, 1, 1);

        event.setCreationDate(newDate);
        event.setLastActivity(newDate);

        assertEquals(newDate, event.getCreationDate());
        assertNotEquals(creationDateBeforeSet, event.getCreationDate());

        assertEquals(newDate, event.getLastActivity());
        assertNotEquals(lastActivityBeforeSet, event.getLastActivity());
    }

    @Test
    public void testGetExpenses() {
        assertEquals(0, event.getExpenses().size());
        event.setExpenses(newExpenses);
        assertEquals(newExpenses, event.getExpenses());
    }

    @Test
    public void testGetPayments() {
        assertEquals(0, event.getPayments().size());
        event.setPayments(newPayments);
        assertEquals(newPayments, event.getPayments());
    }

    @Test
    public void testSetAvailableTags() {
        Set<Tag> originalTags = event.getTags();
        assertNotEquals(newTags, originalTags);
        event.setAvailableTags(newTags);
        assertEquals(newTags, event.getTags());
    }

    @Test
    public void testSetExpenses() {
        event.setExpenses(newExpenses);
        assertEquals(newExpenses, event.getExpenses());
    }

    @Test
    public void testSetParticipants() {
        event.setParticipants(newParticipants);
        assertEquals(newParticipants, event.getParticipants());
    }

    @Test
    public void testSetPayments() {
        event.setPayments(newPayments);
        assertEquals(newPayments, event.getPayments());
    }

    @Test
    public void testAlternativeConstructor() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2", UUID.randomUUID());
        Set<User> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);
        Event event = new Event("Honeymoon", participants);
        assertEquals(participants, event.getParticipants());
    }

    @Test
    public void testGetInviteCode() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Birthday Party");
        event.addParticipant(user1);
        assertNotNull(event.getInviteCode());
    }

    @Test
    public void testGetTitle() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Conference");
        event.addParticipant(user1);
        assertEquals("Conference", event.getTitle());
    }

    @Test
    public void testGetParticipants() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2", UUID.randomUUID());
        List<User> participants = new ArrayList<>();
        participants.add(user1);
        participants.add(user2);
        Event event = new Event("Wedding");
        event.addParticipant(user1);
        event.addParticipant(user2);
        assertEquals(new HashSet<>(participants), event.getParticipants());
    }

    @Test
    public void testGetTotalDebt() {
        List<String> allParticipants = new ArrayList<>();
        allParticipants.add("Alice");
        allParticipants.add("Gerard");
        allParticipants.add("Barry");
        allParticipants.add("Lisa");

        List<String> n134Participants = new ArrayList<>();
        n134Participants.add("Alice");
        n134Participants.add("Barry");
        n134Participants.add("Lisa");

        Set<User> eventUsers = new HashSet<>();
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        User user2 = new User("Gerard", "gerard@gmail.com", "NL123487659", "biicode2", UUID.randomUUID());
        User user3 = new User("Barry", "barry@gmail.com", "NL121212121", "biicode3", UUID.randomUUID());
        User user4 = new User("Lisa", "lisa@gmail.com", "NL098764321", "biicode4", UUID.randomUUID());
        eventUsers.add(user1);
        eventUsers.add(user2);
        eventUsers.add(user3);
        eventUsers.add(user4);

        Event event = new Event("Drinks", eventUsers);
        event.addTransaction(new Expense("Alice", LocalDate.of(2024, 1, 2), 40.0f, Currency.EUR,
                "Cocktails", allParticipants));
        event.addTransaction(new Expense("Alice", LocalDate.of(2023, 12, 30), 30.0f, Currency.EUR,
                        "Champagne", n134Participants));
        event.addTransaction(new Payment("Barry", LocalDate.of(2024, 1, 1), 10.0f, Currency.EUR,
                "Alice"));

        assertEquals(-40.0f, event.getTotalDebt(user1));
        // Alice had cocktails and champagne and received 10 from Barry, so 40/4 + 30/3 + 10 = 30.0f of debt
        // She also paid for two expenses so her actual debt should be 30 - 40 - 30 = -40.0f

        assertEquals(10.0f, event.getTotalDebt(user2));
        // Gerard only had cocktails, so 40/4 = 10.0f debt

        assertEquals(10.0f, event.getTotalDebt(user3));
        // Barry had cocktails and champagne but already paid 10 to Alice, so 40/4 + 30/3 - 10 = 10.0f of debt

        assertEquals(20.0f, event.getTotalDebt(user4));
        // Lisa had cocktails and champagne, so 40/4 + 30/3 = 20.0f of debt
    }

    @Test
    public void testGetTransactions() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Meeting");
        event.addParticipant(user1);
        assertEquals(new ArrayList<>(), event.transactions());
    }

    @Test
    public void testSetTitle() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Drinks");
        event.addParticipant(user1);
        event.setTitle("Bar");
        assertEquals("Bar", event.getTitle());
    }

    @Test
    public void testAddTag() {
        User user1 = new User("John", "john@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Party");
        event.addParticipant(user1);

        event.addTag(new Tag("Activities", new Color(255, 255, 25)));
        assertEquals(4, event.getTags().size());
        assertEquals(new HashSet<>(
                Arrays.asList(
                        new Tag("Food", new Color(147, 196, 125)),
                        new Tag("Entrance Fees", new Color(74, 134, 232)),
                        new Tag("Travel", new Color(224, 102, 102)),
                        new Tag("Activities", new Color(255, 255, 25))
                )
        ), event.getTags());
    }

    @Test
    public void testGetTags() {
        User user1 = new User("John", "john@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Set<User> users = new HashSet<>();
        users.add(user1);
        Event event = new Event("Party", users);

        assertEquals(3, event.getTags().size());
        assertEquals(new HashSet<>(
                Arrays.asList(
                        new Tag("Food", new Color(147, 196, 125)),
                        new Tag("Entrance Fees", new Color(74, 134, 232)),
                        new Tag("Travel", new Color(224, 102, 102))
                )
        ), event.getTags());
    }

    @Test
    public void testRemoveTag() {
        User user1 = new User("John", "john@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Party");
        event.addParticipant(user1);

        Tag activities = new Tag("Activities", new Color(255, 255, 25));
        event.addTag(activities);
        assertEquals(4, event.getTags().size());

        event.removeTag(activities);
        assertEquals(3, event.getTags().size());

        assertThrows(IllegalArgumentException.class, () -> event.removeTag(activities));
    }

    @Test
    public void testAddAndRemoveParticipants() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2", UUID.randomUUID());
        Set<User> participants = new HashSet<>();
        participants.add(user1);
        Event event = new Event("Vacation");
        event.addParticipant(user1);
        assertEquals(participants, event.getParticipants());
        participants.add(user2);
        event.addParticipant(user2);
        assertEquals(participants, event.getParticipants());
        event.removeParticipant(user1);
        participants.remove(user1);
        assertEquals(participants, event.getParticipants());
    }

    @Test
    void testAddAndRemoveNull() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Vacation");
        event.addParticipant(user1);

        int size = event.getParticipants().size();
        assertFalse(event.addParticipant(null));
        assertEquals(size, event.getParticipants().size());
        assertFalse(event.removeParticipant(null));
        assertEquals(size, event.getParticipants().size());
    }

    @Test
    void testAddAndRemoveTransaction() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Vacation");
        event.addParticipant(user1);

        assertFalse(event.addTransaction(null));

        Payment payment = new Payment(LocalDate.of(2021, 1, 1), 100, Currency.EUR,
                "David", "David");

        assertEquals(0, event.transactions().size());
        assertTrue(event.addTransaction(payment));
        assertEquals(1, event.transactions().size());
        assertEquals(payment, event.transactions().getFirst());

        assertFalse(event.addTransaction(null));
        assertEquals(1, event.transactions().size());
        assertEquals(payment, event.transactions().getFirst());

        assertFalse(event.removeTransaction(null));
        assertEquals(1, event.transactions().size());
        assertEquals(payment, event.transactions().getFirst());

        Payment payment2 = new Payment(LocalDate.of(2021, 1, 1), 500, Currency.EUR,
                "David", "David");
        assertFalse(event.removeTransaction(payment2));
        assertEquals(1, event.transactions().size());
        assertEquals(payment, event.transactions().getFirst());

        Payment paymentIdentical = new Payment(LocalDate.of(2021, 1, 1), 100, Currency.EUR,
                "David", "David");
        assertTrue(event.removeTransaction(paymentIdentical));
        assertEquals(0, event.transactions().size());
    }

    @Test
    public void testGetExpensesByParticipant() {
        List<String> allParticipants = new ArrayList<>();
        allParticipants.add("John");
        allParticipants.add("Mike");
        allParticipants.add("Pam");
        allParticipants.add("Amanda");

        List<String> n234Participants = new ArrayList<>();
        n234Participants.add("Mike");
        n234Participants.add("Pam");
        n234Participants.add("Amanda");

        Set<User> eventUsers = new HashSet<>();
        User user1 = new User("John", "john@gmail.com", "NL567856789", "biicode1", UUID.randomUUID());
        User user2 = new User("Mike", "mike@gmail.com", "NL123412349", "biicode2", UUID.randomUUID());
        User user3 = new User("Pam", "pam@gmail.com", "NL121882188", "biicode3", UUID.randomUUID());
        User user4 = new User("Amanda", "amanda@gmail.com", "NL098711321", "biicode4", UUID.randomUUID());
        eventUsers.add(user1);
        eventUsers.add(user2);
        eventUsers.add(user3);
        eventUsers.add(user4);

        Expense rockClimbing = new Expense("John", LocalDate.of(2020, 3, 5), 100.0f, Currency.EUR,
                "Rock Climbing", allParticipants);
        Expense hiringEquipment = new Expense("Mike", LocalDate.of(2023, 12, 30), 21.0f, Currency.EUR,
                "Hiring Equipment", n234Participants);
        Payment payment = new Payment("Amanda", LocalDate.of(2024, 1, 1), 20.0f, Currency.EUR,
                "Pam");
        Event event = new Event("Group Activities", eventUsers);
        event.addTransaction(rockClimbing);
        event.addTransaction(hiringEquipment);
        event.addTransaction(payment);

        List<Expense> johnExpenses = new ArrayList<>();
        johnExpenses.add(rockClimbing);
        List<Expense> mikeExpenses = new ArrayList<>();
        mikeExpenses.add(rockClimbing);
        mikeExpenses.add(hiringEquipment);
        List<Expense> pamExpenses = new ArrayList<>();
        pamExpenses.add(rockClimbing);
        pamExpenses.add(hiringEquipment);
        List<Expense> amandaExpenses = new ArrayList<>();
        amandaExpenses.add(rockClimbing);
        amandaExpenses.add(hiringEquipment);

        assertEquals(johnExpenses, event.getExpensesByParticipant("John"));
        assertEquals(mikeExpenses, event.getExpensesByParticipant("Mike"));
        assertEquals(pamExpenses, event.getExpensesByParticipant("Pam"));
        assertEquals(amandaExpenses, event.getExpensesByParticipant("Amanda"));
        assertThrows(IllegalArgumentException.class, () -> event.getExpensesByParticipant("Clark"));
    }

    @Test
    public void testGetExpensesByTag() {
        List<String> allParticipants = new ArrayList<>();
        allParticipants.add("John");
        allParticipants.add("Mike");
        allParticipants.add("Pam");
        allParticipants.add("Amanda");

        List<String> n234Participants = new ArrayList<>();
        n234Participants.add("Mike");
        n234Participants.add("Pam");
        n234Participants.add("Amanda");

        Set<User> eventUsers = new HashSet<>();
        User user1 = new User("John", "john@gmail.com", "NL567856789", "biicode1", UUID.randomUUID());
        User user2 = new User("Mike", "mike@gmail.com", "NL123412349", "biicode2", UUID.randomUUID());
        User user3 = new User("Pam", "pam@gmail.com", "NL121882188", "biicode3", UUID.randomUUID());
        User user4 = new User("Amanda", "amanda@gmail.com", "NL098711321", "biicode4", UUID.randomUUID());
        eventUsers.add(user1);
        eventUsers.add(user2);
        eventUsers.add(user3);
        eventUsers.add(user4);

        Expense rockClimbing = new Expense("John", LocalDate.of(2020, 3, 5), 100.0f, Currency.EUR,
                "Rock Climbing", allParticipants);
        Expense hiringEquipment = new Expense("Mike", LocalDate.of(2023, 12, 30), 21.0f, Currency.EUR,
                "Hiring Equipment", n234Participants);
        Payment payment = new Payment("Amanda", LocalDate.of(2024, 1, 1), 20.0f, Currency.EUR,
                "Pam");
        Expense lunch = new Expense("Pam", LocalDate.of(2020, 3, 5), 36.0f, Currency.EUR,
                "Lunch after the climbing", allParticipants);
        Expense milkshakes = new Expense("Mike", LocalDate.of(2020, 3, 5), 12.0f, Currency.EUR,
                "Milkshakes on the way home", allParticipants);
        Event event = new Event("Group Activities", eventUsers);
        event.addTransaction(rockClimbing);
        event.addTransaction(hiringEquipment);
        event.addTransaction(payment);
        event.addTransaction(lunch);
        event.addTransaction(milkshakes);

        Tag addedCosts = new Tag("Additional Costs", new Color(100, 100, 100));
        hiringEquipment.addTag(addedCosts);
        Iterator<Tag> iterator = event.getTags().iterator();
        Tag food = iterator.next();
        Tag entranceFees = iterator.next();

        rockClimbing.addTag(entranceFees);
        lunch.addTag(food);
        milkshakes.addTag(food);

        assertThrows(IllegalArgumentException.class, () -> event.getExpensesByTag(addedCosts));

        List<Expense> foodList = new ArrayList<>();
        foodList.add(lunch);
        foodList.add(milkshakes);
        List<Expense> entranceFeesList = new ArrayList<>();
        entranceFeesList.add(rockClimbing);
        List<Expense> travelList = new ArrayList<>();
        List<Expense> addedCostsList = new ArrayList<>();
        addedCostsList.add(hiringEquipment);

        Tag travel = iterator.next();
        event.addTag(addedCosts);
        assertEquals(foodList, event.getExpensesByTag(food));
        assertEquals(entranceFeesList, event.getExpensesByTag(entranceFees));
        assertEquals(travelList, event.getExpensesByTag(travel));
        assertEquals(addedCostsList, event.getExpensesByTag(addedCosts));

        rockClimbing.removeTag(entranceFees);
        assertEquals(new ArrayList<>(), event.getExpensesByTag(entranceFees));
    }

    @Test
    public void testEquals() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Event event = new Event("Football Game");
        event.addParticipant(user1);

        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2", UUID.randomUUID());
        Event event2 = new Event("Picnic");
        event2.addParticipant(user2);

        //event3 and event should not be equal, as the invite code should be different
        Event event3 = new Event("Football Game");
        event.addParticipant(user1);
        assertNotEquals(event, event2);
        assertNotEquals(event, event3);
        assertEquals(event, event);
        assertNotEquals(event, null);
        assertNotEquals(event, "event");
    }

    @Test
    public void testHashCode() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Set<User> users = new HashSet<>();
        users.add(user1);
        Event event = new Event("Football Game", users);

        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2", UUID.randomUUID());
        Set<User> users2 = new HashSet<>();
        users.add(user2);
        Event event2 = new Event("Picnic", users2);
        assertNotEquals(event.hashCode(), event2.hashCode());
    }

    @Test
    public void testToString() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1", UUID.randomUUID());
        Set<User> users = new HashSet<>();
        users.add(user1);
        Event event = new Event("Football Game", users);
        List<String> participants = new ArrayList<>();
        participants.add("David");
        participants.add("Mike");
        event.addTransaction(new Expense("David", LocalDate.of(2020, 2, 2), 10.0f, Currency.EUR,
                "Ice cream and coffee", participants));

        assertEquals("Event{inviteCode='" + event.getInviteCode() + "', title='Football Game', "
                + "participants=[User{name='David', email='david@gmail.com', "
                + "IBAN='NL123456789', BIC='biicode1', eventID=" + user1.getEventID()
                + "}], commons.transactions=[Expense{Transaction{owner = "
                + "'David', date = '2020-02-02', amount = 10.0}, description='Ice cream and coffee', "
                + "debts={Mike=5.0, David=-5.0}}]}", event.toString());
    }
}
