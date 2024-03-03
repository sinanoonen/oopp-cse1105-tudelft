package server.api;

import commons.User;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
            System.out.println("/users: Received bad GET request");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("/users: Received valid GET request");
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
        if (isNullOrEmpty(email)) {
            System.out.println("/users: Received bad PUT request");
            return ResponseEntity.badRequest().build();
        }
        var response = getUserByEmail(email);
        if (response.getStatusCode().isError() || update == null) {
            System.out.println("/users: Received bad PUT request");
            return response;
        }
        System.out.println("/users: Received valid PUT request");
        User user = response.getBody();
        assert user != null;
        String name = isNullOrEmpty(update.getName()) ? user.getName() : update.getName();
        String iban = isNullOrEmpty(update.getIban()) ? user.getIban() : update.getIban();
        String bic = isNullOrEmpty(update.getBic()) ? user.getBic() : update.getBic();
        return repo.updateUser(name, iban, bic, email) == 1
                ? ResponseEntity.ok(new User(name, email, iban, bic))
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
            System.out.println("/users: Received bad POST request");
            return ResponseEntity.badRequest().build();
        }
        System.out.println("/users: Received valid POST request:");
        System.out.println(user);
        User saved = repo.save(user);
        return ResponseEntity.ok(saved);
    }

    /**
     * Endpoint to remove a user from the database.
     *
     * @param email email of user to delete
     */
    @DeleteMapping("/{email}")
    public void deleteUser(@PathVariable String email) {
        int res = repo.deleteUserByEmail(email);
        System.out.println("Received " + (res == 1 ? "valid" : "bad") + " DELETE request");
        if (res == 1) {
            System.out.println("Deleting user " + email);
        }
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
