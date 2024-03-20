package server.api;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import server.database.EventRepository;

import java.util.UUID;

@Controller
public class WebSocketEventController {

    private final EventRepository repo;

    public WebSocketEventController(EventRepository repo) {
        this.repo = repo;
    }

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
