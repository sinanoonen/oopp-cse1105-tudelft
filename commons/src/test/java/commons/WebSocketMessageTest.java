package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketMessageTest {
    @Test
    void testConstructorAndGetContent() {
        String testContent = "Test Message";
        WebSocketMessage message = new WebSocketMessage(testContent);
        assertEquals(testContent, message.getContent());
    }

    @Test
    void testSetContent() {
        String testContent = "Test Message";
        WebSocketMessage message = new WebSocketMessage();
        message.setContent(testContent);
        assertEquals(testContent, message.getContent());
    }

    @Test
    void testEquals() {
        String content = "Test Message";
        WebSocketMessage message1 = new WebSocketMessage(content);
        WebSocketMessage message2 = new WebSocketMessage(content);
        assertEquals(message1, message2);
    }

    @Test
    void testHashCode() {
        String content = "Test Message";
        WebSocketMessage message1 = new WebSocketMessage(content);
        WebSocketMessage message2 = new WebSocketMessage(content);
        assertEquals(message1.hashCode(), message2.hashCode());
    }

    @Test
    void testNotEqualsWithDifferentContent() {
        WebSocketMessage message1 = new WebSocketMessage("Message 1");
        WebSocketMessage message2 = new WebSocketMessage("Message 2");
        assertNotEquals(message1, message2);
    }

    @Test
    void testNotEqualsWithNull() {
        WebSocketMessage message = new WebSocketMessage("Test Message");
        assertNotEquals(message, null);
    }

    @Test
    void testNotEqualsWithDifferentObjectType() {
        WebSocketMessage message = new WebSocketMessage("Test Message");
        assertNotEquals(message, new Object());
    }
}