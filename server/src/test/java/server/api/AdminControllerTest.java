package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.AdminPasswordService;

/**
 * This is used to test the Admin Controller.
 */
public class AdminControllerTest {

    private AdminPasswordService adminPasswordService;
    private AdminController adminController;
    private String validPassword;

    /**
     * This is the setUp for testing.
     */
    @BeforeEach
    public void setUp() {
        adminPasswordService = new AdminPasswordService();
        validPassword = adminPasswordService.getAdminPassword();
        adminController = new AdminController(adminPasswordService);
    }

    @Test
    public void whenValidPassword_thenReturnsStatusOk() {
        ResponseEntity<?> response = adminController.verifyPassword(validPassword);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void whenInvalidPassword_thenReturnsStatusUnauthorized() {
        String invalidPassword = "invalidPassword";
        ResponseEntity<?> response = adminController.verifyPassword(invalidPassword);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
