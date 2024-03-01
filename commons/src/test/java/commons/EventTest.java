package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
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
        assertEquals(10, event.getInviteCode().toString().length());
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
    public void testAddAndRemoveParticipants() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2");
        List<User> participants = new ArrayList<>();
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
