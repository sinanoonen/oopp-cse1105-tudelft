package server;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Service;

/**
 * This is a service to generate a random password when the server starts.
 */
@Service
public class AdminPasswordService {

    private final String adminPassword;

    public AdminPasswordService() {
        this.adminPassword = generateRandomPassword(16);
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    private String generateRandomPassword(int byteLength) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}