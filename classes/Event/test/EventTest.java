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
}
