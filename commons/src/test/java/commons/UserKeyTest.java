package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * This tests the UserKey class.
 */
public class UserKeyTest {

    @Test
    public void testEmptyConstructor() {
        UserKey key = new UserKey();
        assertNull(key.getEmail());
        assertNull(key.getEventID());
    }

    @Test
    public void testConstructorWithParameters() {
        String email = "test@example.com";
        UUID eventID = UUID.randomUUID();

        UserKey key = new UserKey(email, eventID);

        assertEquals(email, key.getEmail());
        assertEquals(eventID, key.getEventID());
    }

    @Test
    public void testSettersAndGetters() {
        String email = "test@example.com";
        UUID eventID = UUID.randomUUID();

        UserKey key = new UserKey();
        key.setEmail(email);
        key.setEventID(eventID);

        assertEquals(email, key.getEmail());
        assertEquals(eventID, key.getEventID());
    }

    @Test
    public void testEqualsAndHashCode() {
        String email = "test@example.com";
        UUID eventID = UUID.randomUUID();

        UserKey key1 = new UserKey(email, eventID);
        UserKey key2 = new UserKey(email, eventID);
        UserKey key3 = new UserKey("different@example.com", eventID);

        assertEquals(key1, key2);
        assertNotEquals(key1, key3);
        assertEquals(key1.hashCode(), key2.hashCode());
        assertNotEquals(key1.hashCode(), key3.hashCode());

        assertNotEquals(key1, new Object());
        assertNotEquals(key1, null);
    }
}
