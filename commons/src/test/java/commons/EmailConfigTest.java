package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * This is used to test the EmailConfig class.
 */
public class EmailConfigTest {

    @Test
    public void testEmailConfigConstructorAndGetters() {
        EmailConfig emailConfig = new EmailConfig("smtp.example.com", 587, "user@example.com", "password");

        assertEquals("smtp.example.com", emailConfig.getHost());
        assertEquals(587, emailConfig.getPort());
        assertEquals("user@example.com", emailConfig.getUsername());
        assertEquals("password", emailConfig.getPassword());
    }

    @Test
    public void testSetters() {
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setHost("smtp.example.com");
        emailConfig.setPort(587);
        emailConfig.setUsername("user@example.com");
        emailConfig.setPassword("password");

        assertEquals("smtp.example.com", emailConfig.getHost());
        assertEquals(587, emailConfig.getPort());
        assertEquals("user@example.com", emailConfig.getUsername());
        assertEquals("password", emailConfig.getPassword());
    }

    @Test
    public void testIsComplete() {
        EmailConfig completeConfig = new EmailConfig("smtp.example.com", 587, "user@example.com", "password");
        assertTrue(completeConfig.isComplete());

        EmailConfig incompleteConfig = new EmailConfig(null, 0, null, null);
        assertFalse(incompleteConfig.isComplete());
    }

    @Test
    public void testEqualsAndHashCode() {
        EmailConfig config1 = new EmailConfig("smtp.example.com", 587, "user@example.com", "password");
        EmailConfig config2 = new EmailConfig("smtp.example.com", 587, "user@example.com", "password");
        EmailConfig config3 = new EmailConfig("smtp.different.com", 587, "user@example.com", "password");

        assertEquals(config1, config2);
        assertNotEquals(config1, config3);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1.hashCode(), config3.hashCode());
    }
}