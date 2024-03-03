package server.api;

import commons.Event;
import commons.User;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.EventRepository;

/**
 * A controller for events.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository repo;

    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    /**
     * Get all events.
     *
     * @return all events
     */
    @GetMapping(path = { "", "/" })
    public List<Event> getAll() {
        return repo.findAll();
    }

    /**
     * Get an event by its UUID.
     *
     * @param uuid the UUID of the event
     * @return the event
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<Event> getById(@PathVariable("uuid") UUID uuid) {
        if (!repo.existsById(uuid)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(uuid).get());
    }

    /**
     * Add an event.
     *
     * @param event the event to add
     * @return the added event
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Event> add(@RequestBody Event event) {

        if (event.getInviteCode() == null || isNullOrEmpty(event.getTitle())
            || event.getTags() == null || event.getParticipants() == null
            || event.getTransactions() == null) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = repo.save(event);
        return ResponseEntity.ok(saved);
    }


    /**
     * Adds a user to an event.
     *
     * @param uuid the UUID of the event
     * @param user the user to add
     * @return the updated event
     */
    @PutMapping("/{uuid}/users")
    public ResponseEntity<Event> addUser(@PathVariable("uuid") UUID uuid, @RequestBody User user) {
        if (!repo.existsById(uuid)) {
            return ResponseEntity.badRequest().build();
        }

        Event event = repo.findById(uuid).get();
        event.addParticipant(user);
        repo.save(event);
        return ResponseEntity.ok(event);
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param s the string s
     * @return true if the string is null or empty
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}