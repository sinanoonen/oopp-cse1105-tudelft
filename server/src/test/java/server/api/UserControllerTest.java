package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import commons.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.UserRepository;

/**
 * Tests for UserController.
 */
public class UserControllerTest {

    private UserRepository repo;
    private UserController controller;

    private User[] users;

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        this.repo = new TestUserRepository();
        this.controller = new UserController(repo);
        users = new User[3];
        users[0] = new User("Filip", "fkeerberg@tudelft.nl", "iban_filip", "bic_filip", UUID.randomUUID());
        users[1] = new User("Emilio", "ecostanza@tudelft.nl", "iban_emilio", "bic_emilio", UUID.randomUUID());
        users[2] = new User("Sinan", "sönen@tudelft.nl", null, "bic_sinan", UUID.randomUUID());
    }

    @Test
    public void rootGetReturns() {
        List<User> res = controller.getAllUsers();
        assertNotNull(res);
    }

    @Test
    public void addUserTest() {
        var res = controller.addUser(users[0]);
        assertNotEquals(BAD_REQUEST, res.getStatusCode());
        assertEquals(users[0], res.getBody());
    }

    @Test
    public void cannotAddNullUser() {
        var res = controller.addUser(null);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void cannotAddIncompleteUser() {
        var res = controller.addUser(users[2]);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void getAllUsers() {
        List<User> res = controller.getAllUsers();
        assertTrue(res.isEmpty());
        controller.addUser(users[0]);
        res = controller.getAllUsers();
        assertEquals(Collections.singletonList(users[0]), res);
        controller.addUser(users[1]);
        res = controller.getAllUsers();
        assertEquals(Arrays.asList(users[0], users[1]), res);
    }

    @Test
    public void validGetUser() {
        controller.addUser(users[0]);
        controller.addUser(users[1]);
        var res = controller.getUserByEmail(users[0].getEmail());
        assertNotEquals(BAD_REQUEST, res.getStatusCode());
        assertEquals(users[0], res.getBody());
    }

    @Test
    public void invalidGetUser() {
        controller.addUser(users[0]);
        controller.addUser(users[1]);
        var res = controller.getUserByEmail("randomwrongemail@tudelft.nl");
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void getUserNull() {
        controller.addUser(users[0]);
        controller.addUser(users[1]);
        var res = controller.getUserByEmail(null);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void validEditUser() {
        controller.addUser(users[0]);
        controller.addUser(users[1]);
        User edits = new User("Nico", null, null, null, null);
        var res = controller.editUser(users[1].getEmail(), edits);
        assertNotEquals(BAD_REQUEST, res.getStatusCode());
        User expected = new User("Nico", users[1].getEmail(), users[1].getIban(), users[1].getBic(), users[1].getEventID());
        assertEquals(expected, res.getBody());
    }

    @Test
    public void editUserEmail() {
        controller.addUser(users[0]);
        User edits = new User(null, "newemail@tudelft.nl", null, "new_bic", UUID.randomUUID());
        var res = controller.editUser(users[0].getEmail(), edits);
        assertEquals(BAD_REQUEST, res.getStatusCode());
        // Check unchanged
        res = controller.getUserByEmail(users[0].getEmail());
        assertNotEquals(BAD_REQUEST, res.getStatusCode());
        assertEquals(users[0].getBic(), res.getBody().getBic());
    }

    @Test
    public void editWithoutEdits() {
        controller.addUser(users[0]);
        User edits = new User(null, null, null, null, null);
        var res = controller.editUser(users[0].getEmail(), edits);
        assertNotEquals(BAD_REQUEST, res.getStatusCode());
        assertEquals(users[0], res.getBody());
    }

    @Test
    public void nullEdits() {
        controller.addUser(users[0]);
        var res = controller.editUser(users[0].getEmail(), null);
        assertNotEquals(BAD_REQUEST, res.getStatusCode());
        assertEquals(users[0], res.getBody());
    }

    @Test
    public void validDeleteTest() {
        controller.addUser(users[0]);
        controller.addUser(users[1]);
        List<User> allUsers = controller.getAllUsers();
        assertEquals(2, allUsers.size());
        var res = controller.deleteUser(users[1].getEmail());
        assertNotEquals(BAD_REQUEST, res.getStatusCode());
        allUsers = controller.getAllUsers();
        assertEquals(Collections.singletonList(users[0]), allUsers);
    }

    @Test
    public void badDelete() {
        controller.addUser(users[0]);
        controller.addUser(users[1]);
        var res = controller.deleteUser("anotherwrongemail@tudelft.nl");
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void deleteNull() {
        controller.addUser(users[0]);
        controller.addUser(users[1]);
        var res = controller.deleteUser(null);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
}
