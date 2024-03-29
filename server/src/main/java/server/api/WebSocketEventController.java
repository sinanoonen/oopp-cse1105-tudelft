package server.api;

import commons.WebSocketMessage;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;

/**
 * This is the web socket event controller.
 */
@Controller
public class WebSocketEventController {

    private final EventRepository repo;
    private final SimpMessagingTemplate template;

    /**
     * Constructor for the web socket controller.
     *
     * @param repo the event repository
     */
    public WebSocketEventController(EventRepository repo, SimpMessagingTemplate template) {
        this.repo = repo;
        this.template = template;
    }

    /**
     * Deletes an event.
     *
     * @param uuidString the uuid string of the event
     */
    @MessageMapping("/deleteEvent")
    public void deleteEvent(@Payload String uuidString) {
        if (uuidString == null) {
            throw new IllegalArgumentException();
        }
        UUID uuid = UUID.fromString(uuidString);
        if (repo.existsById(uuid)) {
            repo.deleteById(uuid);
            WebSocketMessage message = new WebSocketMessage("Event deleted: " + uuid);
            template.convertAndSend("/topic/eventsUpdated", message);
        }
    }
}
