package server.api;

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
     * @param uuid the uuid of the event
     */
    @MessageMapping("/deleteEvent")
    public void deleteEvent(@Payload UUID uuid) {
        if (repo.existsById(uuid)) {
            repo.deleteById(uuid);
            template.convertAndSend("/topic/eventsUpdated", "Event deleted: " + uuid);
        }
    }
}
