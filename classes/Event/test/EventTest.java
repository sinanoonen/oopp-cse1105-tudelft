import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventTest {

    @Test
    public void testGetInviteCode() {
        List<String> participants = new ArrayList<>();
        participants.add("Alice");
        Event event = new Event("Birthday Party", participants);
        assertEquals(10, event.getInviteCode().length());
    }

    @Test
    public void testGetTitle() {
        List<String> participants = new ArrayList<>();
        participants.add("Bob");
        Event event = new Event("Conference", participants);
        assertEquals("Conference", event.getTitle());
    }

    @Test
    public void testGetParticipants() {
        List<String> participants = new ArrayList<>();
        participants.add("Charlie");
        participants.add("David");
        Event event = new Event("Wedding", participants);
        assertEquals(participants, event.getParticipants());
    }

    @Test
    public void testGetTransactions() {
        List<String> participants = new ArrayList<>();
        participants.add("Emma");
        Event event = new Event("Meeting", participants);
        assertEquals(new ArrayList<>(), event.getTransactions());
    }

    @Test
    public void testSetInviteCode() {
        List<String> participants = new ArrayList<>();
        participants.add("Filip");
        Event event = new Event("Lunch", participants);
        event.setInviteCode("1234567890");
        assertEquals("1234567890", event.getInviteCode());
    }

    @Test
    public void testSetTitle() {
        List<String> participants = new ArrayList<>();
        participants.add("George");
        Event event = new Event("Drinks", participants);
        event.setTitle("Bar");
        assertEquals("Bar", event.getTitle());
    }

    @Test
    public void testSetParticipants() {
        List<String> participants = new ArrayList<>();
        participants.add("Hanna");
        Event event = new Event("Vacation", participants);
        List<String> particpants2 = new ArrayList<>();
        participants2.add("Ivo");
        participants2.add("Jolyne");
        event.setParticipants(particpants2);
        assertEquals(particpants2, event.getParticipants());
    }

    @Test
    public void testEquals() {
        List<String> participants = new ArrayList<>();
        participants.add("Kevin");
        participants.add("Larry");
        Event event = new Event("Football Game", participants);
        List<String> participants2 = new ArrayList<>();
        participants2.add("Mark");
        participants2.add("Nina");
        Event event2 = new Event("Picnic", participants2);
        //event3 and event should not be equal, as the invite code should be different
        Event event3 = new Event("Football Game", participants);
        assertNotEquals(event, event2);
        assertNotEquals(event, event3);
        assertEquals(event, event);
    }
}
