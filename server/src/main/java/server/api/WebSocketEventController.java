package server.api;

import java.util.UUID;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;

/**
 * This is the web socket event controller.
 */
@Controller
public class WebSocketEventController {

    private final EventRepository repo;

    /**
     * Constructor for the web socket controller.
     *
     * @param repo the event repository
     */
    public WebSocketEventController(EventRepository repo) {
        this.repo = repo;
    }

    /**
     * Deletes an event.
     *
     * @param uuid the uuid of the event
     * @return a message with the result
     */
    @MessageMapping("/deleteEvent")
    public String deleteEvent(@Payload UUID uuid) {
        if (repo.existsById(uuid)) {
            repo.deleteById(uuid);
            return "Deleted event with UUID: " + uuid;
        } else {
            return "Event not found with UUID: " + uuid;
        }
    }
}
