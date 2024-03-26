package server.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

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