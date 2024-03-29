package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import commons.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.UserRepository;


class UserControllerTest {
    private UserRepository mockRepo;
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockRepo = mock(UserRepository.class);
        userController = new UserController(mockRepo);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("John Doe", "john@example.com", "1234", "5678", null));

        when(mockRepo.findAll()).thenReturn(users);

        List<User> result = userController.getAllUsers();
        assertEquals(users, result);
    }

    @Test
    void testGetUserByEmail() {
        String email = "john@example.com";
        User user = new User("John Doe", email, "1234", "5678", null);

        List<User> users = new ArrayList<>();
        users.add(user);

        when(mockRepo.getUserByEmail(email)).thenReturn(users);

        ResponseEntity<User> responseEntity = userController.getUserByEmail(email);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user, responseEntity.getBody());
    }

    @Test
    void testEditUser() {
        String email = "john@example.com";
        User existingUser = new User("John Doe", email, "1234", "5678", null);
        User updatedUser = new User("Jane Smith", email, "4321", "8765", null);

        when(mockRepo.updateUser(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()))
                .thenReturn(1);

        ResponseEntity<User> responseEntity = userController.editUser(email, updatedUser);
        assertNotEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotEquals(updatedUser, responseEntity.getBody());
    }

    @Test
    void testEditUser_NullUpdate() {
        String email = "john@example.com";

        ResponseEntity<User> responseEntity = userController.editUser(email, null);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testEditUser_InvalidEmail() {
        String email = "";
        User user = new User("John Doe", email, "1234", "5678", null);

        ResponseEntity<User> responseEntity = userController.editUser(email, user);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testEditUser_BadRequestFromRepository() {
        String email = "john@example.com";
        User user = new User("John Doe", email, "1234", "5678", null);

        when(mockRepo.getUserByEmail(email)).thenReturn(new ArrayList<>());

        ResponseEntity<User> responseEntity = userController.editUser(email, user);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testAddUser() {
        User user = new User("John Doe", "john@example.com", "1234", "5678", null);

        when(mockRepo.save(user)).thenReturn(user);

        ResponseEntity<User> responseEntity = userController.addUser(user);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user, responseEntity.getBody());
    }

    @Test
    void testAddUser_NullUser() {
        ResponseEntity<User> responseEntity = userController.addUser(null);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testAddUser_MissingInformation() {
        User user = new User("", "", "", "", null);

        ResponseEntity<User> responseEntity = userController.addUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testDeleteUser() {
        String email = "john@example.com";

        when(mockRepo.deleteUserByEmail(email)).thenReturn(1);

        ResponseEntity<User> responseEntity = userController.deleteUser(email);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testDeleteUser_UserNotFound() {
        String email = "unknown@example.com";

        when(mockRepo.deleteUserByEmail(email)).thenReturn(0);

        ResponseEntity<User> responseEntity = userController.deleteUser(email);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}