package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import server.AdminPasswordService;

import static org.junit.jupiter.api.Assertions.*;

public class AdminControllerTest {

    private AdminPasswordService adminPasswordService;
    private AdminController adminController;
    private String validPassword;

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
