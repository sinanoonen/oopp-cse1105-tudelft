package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import commons.transactions.Payment;
import commons.transactions.Tag;
import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import java.util.HashMap;
//import java.util.Map;
import org.junit.jupiter.api.Test;



/**
 * A test for the Event class.
 */
public class EventTest {

    @Test
    public void testGetInviteCode() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Birthday Party", user1);
        assertNotNull(event.getInviteCode());
    }

    @Test
    public void testGetTitle() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Conference", user1);
        assertEquals("Conference", event.getTitle());
    }

    @Test
    public void testGetParticipants() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2");
        List<User> participants = new ArrayList<>();
        participants.add(user1);
        participants.add(user2);
        Event event = new Event("Wedding", user1);
        event.addParticipant(user2);
        assertEquals(new HashSet<>(participants), event.getParticipants());
    }

    @Test
    public void testAlternativeConstructor() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2");
        Set<User> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);
        Event event = new Event("Honeymoon", participants);
        assertEquals(participants, event.getParticipants());
    }

    @Test
    public void testGetTransactions() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Meeting", user1);
        assertEquals(new ArrayList<>(), event.getTransactions());
    }

    @Test
    public void testSetTitle() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Drinks", user1);
        event.setTitle("Bar");
        assertEquals("Bar", event.getTitle());
    }

    @Test
    public void testGetTags() {
        User user1 = new User("John", "john@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Party", user1);

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
    public void testAddTag() {
        User user1 = new User("John", "john@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Party", user1);

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
    public void testRemoveTag() {
        User user1 = new User("John", "john@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Party", user1);

        Tag activities = new Tag("Activities", new Color(255, 255, 25));
        event.addTag(activities);
        assertEquals(4, event.getTags().size());

        event.removeTag(activities);
        assertEquals(3, event.getTags().size());

        assertThrows(IllegalArgumentException.class, () -> event.removeTag(activities));
    }


    @Test
    public void testAddAndRemoveParticipants() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2");
        Set<User> participants = new HashSet<>();
        participants.add(user1);
        Event event = new Event("Vacation", user1);
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
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Vacation", user1);

        int size = event.getParticipants().size();
        assertFalse(event.addParticipant(null));
        assertEquals(size, event.getParticipants().size());
        assertFalse(event.removeParticipant(null));
        assertEquals(size, event.getParticipants().size());
    }

    @Test
    void testAddAndRemoveTransaction() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Vacation", user1);

        assertFalse(event.addTransaction(null));

        Payment payment = new Payment("David",
                LocalDate.of(2021, 1, 1), 100,
                "David");

        assertEquals(0, event.getTransactions().size());
        assertTrue(event.addTransaction(payment));
        assertEquals(1, event.getTransactions().size());
        assertEquals(payment, event.getTransactions().getFirst());

        assertFalse(event.addTransaction(null));
        assertEquals(1, event.getTransactions().size());
        assertEquals(payment, event.getTransactions().getFirst());

        assertFalse(event.removeTransaction(null));
        assertEquals(1, event.getTransactions().size());
        assertEquals(payment, event.getTransactions().getFirst());

        Payment payment2 = new Payment("David",
                LocalDate.of(2021, 1, 1), 500,
                "David");
        assertFalse(event.removeTransaction(payment2));
        assertEquals(1, event.getTransactions().size());
        assertEquals(payment, event.getTransactions().getFirst());

        Payment paymentIdentical = new Payment("David",
                LocalDate.of(2021, 1, 1), 100,
                "David");
        assertTrue(event.removeTransaction(paymentIdentical));
        assertEquals(0, event.getTransactions().size());
    }

    @Test
    public void testEquals() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        Event event = new Event("Football Game", user1);

        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2");
        Event event2 = new Event("Picnic", user2);

        //event3 and event should not be equal, as the invite code should be different
        Event event3 = new Event("Football Game", user1);
        assertNotEquals(event, event2);
        assertNotEquals(event, event3);
    }
}
