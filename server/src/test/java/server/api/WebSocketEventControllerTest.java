package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import commons.Event;
import commons.User;
import commons.WebSocketMessage;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



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

        assertEquals("/topic/event", messagingTemplate.getLastDestination());
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

    @Test
    public void deleteEventWithInvalidUUID() {
        assertThrows(IllegalArgumentException.class, () -> controller.deleteEvent("invalid-uuid"));

        assertNull(messagingTemplate.getLastDestination());
        assertNull(messagingTemplate.getLastPayload());
    }

    @Test
    public void deleteEventWithNullUUID() {
        assertThrows(IllegalArgumentException.class, () -> controller.deleteEvent(null));

        assertNull(messagingTemplate.getLastDestination());
        assertNull(messagingTemplate.getLastPayload());
    }

    @Test
    public void deleteEventWithEmptyUUID() {
        assertThrows(IllegalArgumentException.class, () -> controller.deleteEvent(""));

        assertNull(messagingTemplate.getLastDestination());
        assertNull(messagingTemplate.getLastPayload());
    }

    private static Event getEvent(String title) {
        Event event = new Event(title);
        event.addParticipant(new User("testName", "testMail", "testIBAN", "testBic", UUID.randomUUID()));
        return event;
    }
}