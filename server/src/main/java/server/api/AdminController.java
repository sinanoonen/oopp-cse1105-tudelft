package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.AdminPasswordService;

@RestController
@RequestMapping("/api/auth")
public class AdminController {
    private final AdminPasswordService passwordService;

    public AdminController(AdminPasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<?> verifyPassword(@RequestBody String request) {
        boolean isValid = passwordService.getAdminPassword().equals(request);
        return isValid ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
