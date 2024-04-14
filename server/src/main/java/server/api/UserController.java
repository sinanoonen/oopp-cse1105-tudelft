package server.api;

import commons.User;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.ListenerService;
import server.database.UserRepository;

/**
 * A controller to access the users of the database.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repo;
    private final ListenerService listenerService;

    public UserController(UserRepository repo, ListenerService listenerService) {
        this.repo = repo;
        this.listenerService = listenerService;
    }

    /**
     * Endpoint to get a list of all users.
     *
     * @return found user
     */
    @GetMapping(path = {"", "/"})
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    /**
     * Endpoint to get a user by their email.
     *
     * @param email email to query by
     * @return found user
     */
    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        List<User> results = repo.getUserByEmail(email);
        if (results == null || results.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(results.getFirst());
    }

    /**
     * Updates a user.
     *
     * @param email email of user to modify
     * @param update updated user information
     * @return updated user
     */
    @PutMapping("/{email}")
    public ResponseEntity<User> editUser(@PathVariable String email, @RequestBody User update) {
        if (update == null) {
            update = new User(null, null, null, null, null);
        }

        if (isNullOrEmpty(email) || (update.getEmail() != null && !update.getEmail().equals(email))) {
            return ResponseEntity.badRequest().build();
        }
        var response = getUserByEmail(email);
        if (response.getStatusCode().isError()) {
            return response;
        }
        User user = response.getBody();
        assert user != null;
        String name = isNullOrEmpty(update.getName()) ? user.getName() : update.getName();
        String iban = isNullOrEmpty(update.getIban()) ? user.getIban() : update.getIban();
        String bic = isNullOrEmpty(update.getBic()) ? user.getBic() : update.getBic();
        UUID eventID = update.getEventID() == null ? user.getEventID() : update.getEventID();
        listenerService.notifyListeners(update.getEventID().toString());
        return repo.updateUser(name, iban, bic, email) == 1
                ? ResponseEntity.ok(new User(name, email, iban, bic, eventID))
                : ResponseEntity.badRequest().build();
    }

    /**
     * Endpoint to add a new user to the database.
     *
     * @param user user to be added
     * @return added user
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<User> addUser(@RequestBody User user) {
        if (user == null
            || isNullOrEmpty(user.getName())
            || isNullOrEmpty(user.getEmail())
            || isNullOrEmpty(user.getIban())
            || isNullOrEmpty(user.getBic())
        ) {
            return ResponseEntity.badRequest().build();
        }
        User saved = repo.save(user);
        listenerService.notifyListeners(user.getEventID().toString());
        return ResponseEntity.ok(saved);
    }

    /**
     * Endpoint to remove a user from the database.
     *
     * @param email email of user to delete
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<User> deleteUser(@PathVariable String email) {
        int res = repo.deleteUserByEmail(email);
        if (res == 1) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
