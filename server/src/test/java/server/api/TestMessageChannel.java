package server.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

/**
 * This is a test class to simulate a message channel.
 */
public class TestMessageChannel implements MessageChannel {
    private Message<?> lastSentMessage;

    @Override
    public boolean send(Message<?> message, long timeout) {
        this.lastSentMessage = message;
        return true;
    }

    public Message<?> getLastSentMessage() {
        return lastSentMessage;
    }
}