package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import server.AdminPasswordService;

/**
 * Tests for the AdminPasswordService.
 */
public class TestAdminPasswordService {

    @Test
    public void testGeneratedPassword() {
        AdminPasswordService passwordService = new AdminPasswordService();

        String password = passwordService.getAdminPassword();

        assertNotNull(password);

        assertEquals(22, password.length());

        AdminPasswordService anotherPasswordService = new AdminPasswordService();
        String anotherPassword = anotherPasswordService.getAdminPassword();
        assertNotEquals(password, anotherPassword);
    }
}
