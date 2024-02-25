import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * A test for the Event class.
 */
public class EventTest {

    @Test
    public void testGetInviteCode() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1");
        Map<User, Float> participants = new HashMap<>();
        participants.put(user1, 0f);
        Event event = new Event("Birthday Party", participants);
        assertEquals(10, event.getInviteCode().length());
    }

    @Test
    public void testGetTitle() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1");
        Map<User, Float> participants = new HashMap<>();
        participants.put(user1, 0f);
        Event event = new Event("Conference", participants);
        assertEquals("Conference", event.getTitle());
    }

    @Test
    public void testGetParticipants() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2");
        Map<User, Float> participants = new HashMap<>();
        participants.put(user1, 0f);
        participants.put(user2, 0f);
        Event event = new Event("Wedding", participants);
        assertEquals(participants, event.getParticipants());
    }

    @Test
    public void testGetTransactions() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        Map<User, Float> participants = new HashMap<>();
        participants.put(user1, 0f);
        Event event = new Event("Meeting", participants);
        assertEquals(new ArrayList<>(), event.getTransactions());
    }

    @Test
    public void testSetInviteCode() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        Map<User, Float> participants = new HashMap<>();
        participants.put(user1, 0f);
        Event event = new Event("Lunch", participants);
        event.setInviteCode("1234567890");
        assertEquals("1234567890", event.getInviteCode());
    }

    @Test
    public void testSetTitle() {
        User user1 = new User("Alice", "alice@gmail.com", "NL123456789", "biicode1");
        Map<User, Float> participants = new HashMap<>();
        participants.put(user1, 0f);
        Event event = new Event("Drinks", participants);
        event.setTitle("Bar");
        assertEquals("Bar", event.getTitle());
    }

    @Test
    public void testSetParticipants() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        Map<User, Float> participants = new HashMap<>();
        participants.put(user1, 0f);
        Event event = new Event("Vacation", participants);
        Map<User, Float> participants2 = new HashMap<>();
        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2");
        participants2.put(user2, 0f);
        event.setParticipants(participants2);
        assertEquals(participants2, event.getParticipants());
    }

    @Test
    public void testEquals() {
        User user1 = new User("David", "david@gmail.com", "NL123456789", "biicode1");
        Map<User, Float> participants = new HashMap<>();
        participants.put(user1, 0f);
        Event event = new Event("Football Game", participants);

        User user2 = new User("Charlie", "charlie@gmail.com", "NL234567891", "biicode2");
        Map<User, Float> participants2 = new HashMap<>();
        participants.put(user2, 0f);
        Event event2 = new Event("Picnic", participants2);

        //event3 and event should not be equal, as the invite code should be different
        Event event3 = new Event("Football Game", participants);
        assertNotEquals(event, event2);
        assertNotEquals(event, event3);
    }
}
