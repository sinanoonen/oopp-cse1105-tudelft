package server.api;

import commons.Event;
import commons.User;
import commons.WebSocketMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketEventControllerTest {
    private WebSocketEventController controller;
    private TestEventRepository fakeRepo;
    private TestSimpMessagingTemplate messagingTemplate;

    @BeforeEach
    public void setUp() {
        fakeRepo = new TestEventRepository();
        messagingTemplate = new TestSimpMessagingTemplate();
        controller = new WebSocketEventController(fakeRepo, messagingTemplate);
    }

    @Test
    public void deleteEventWhenExists() {
        Event testEvent = getEvent("test");
        fakeRepo.save(testEvent);

        controller.deleteEvent(testEvent.getInviteCode().toString());

        assertEquals("/topic/eventsUpdated", messagingTemplate.getLastDestination());
        WebSocketMessage expectedMessage = new WebSocketMessage("Event deleted: " + testEvent.getInviteCode());
        assertEquals(expectedMessage, messagingTemplate.getLastPayload());
    }

    @Test
    public void deleteEventWhenDoesNotExist() {
        UUID nonExistentEventId = UUID.randomUUID();

        controller.deleteEvent(nonExistentEventId.toString());

        assertFalse(fakeRepo.existsById(nonExistentEventId));

        assertNull(messagingTemplate.getLastDestination());
        assertNull(messagingTemplate.getLastPayload());
    }

    private static Event getEvent(String title) {
        Event event = new Event(title);
        event.addParticipant(new User("testName", "testMail", "testIBAN", "testBic", UUID.randomUUID()));
        return event;
    }
}