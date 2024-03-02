package server.api;

import commons.User;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.UserRepository;

/**
 * A controller to access the users of the database.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
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
            System.out.println("Received bad post request");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("Received valid post request:");
        System.out.println(user);
        User saved = repo.save(user);
        return ResponseEntity.ok(saved);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
