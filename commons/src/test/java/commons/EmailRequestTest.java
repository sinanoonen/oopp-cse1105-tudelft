package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class EmailRequestTest {

    @Test
    public void testEmailRequestConstructorAndGetters() {
        EmailConfig emailConfig = new EmailConfig("smtp.example.com", 587, "user@example.com", "password");
        EmailRequest emailRequest = new EmailRequest(emailConfig, "recipient@example.com", "Subject", "Message");

        assertEquals(emailConfig, emailRequest.getEmailConfig());
        assertEquals("recipient@example.com", emailRequest.getRecipient());
        assertEquals("Subject", emailRequest.getSubject());
        assertEquals("Message", emailRequest.getMessage());
    }

    @Test
    public void testSetters() {
        EmailConfig emailConfig = new EmailConfig();
        EmailRequest emailRequest = new EmailRequest();

        emailRequest.setEmailConfig(emailConfig);
        emailRequest.setRecipient("recipient@example.com");
        emailRequest.setSubject("Subject");
        emailRequest.setMessage("Message");

        assertEquals(emailConfig, emailRequest.getEmailConfig());
        assertEquals("recipient@example.com", emailRequest.getRecipient());
        assertEquals("Subject", emailRequest.getSubject());
        assertEquals("Message", emailRequest.getMessage());
    }

    @Test
    public void testEqualsAndHashCode() {
        EmailConfig emailConfig1 = new EmailConfig("smtp.example.com", 587, "user@example.com", "password");
        EmailRequest emailRequest1 = new EmailRequest(emailConfig1, "recipient@example.com", "Subject", "Message");

        EmailConfig emailConfig2 = new EmailConfig("smtp.example.com", 587, "user@example.com", "password");
        EmailRequest emailRequest2 = new EmailRequest(emailConfig2, "recipient@example.com", "Subject", "Message");

        EmailRequest emailRequest3 = new EmailRequest(emailConfig2, "other@example.com", "Subject", "Message");

        assertEquals(emailRequest1, emailRequest2);
        assertNotEquals(emailRequest1, emailRequest3);
        assertEquals(emailRequest1.hashCode(), emailRequest2.hashCode());
        assertNotEquals(emailRequest1.hashCode(), emailRequest3.hashCode());
    }
}
