package server.api;

import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * This is used to emulate a SimpMessagingTemplate for testing.
 */
public class TestSimpMessagingTemplate extends SimpMessagingTemplate {
    private String lastDestination;
    private Object lastPayload;

    public TestSimpMessagingTemplate() {
        super(new TestMessageChannel());
    }

    @Override
    public void convertAndSend(String destination, Object payload) {
        this.lastDestination = destination;
        this.lastPayload = payload;
    }

    public String getLastDestination() {
        return lastDestination;
    }

    public Object getLastPayload() {
        return lastPayload;
    }
}
