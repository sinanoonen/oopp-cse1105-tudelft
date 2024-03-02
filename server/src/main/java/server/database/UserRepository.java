package server.database;

import commons.User;
import commons.UserKey;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
 * Repository for users.
 */
public interface UserRepository extends JpaRepository<User, UserKey> {
    @Query(value = "SELECT * FROM USERS u WHERE u.EMAIL=?1", nativeQuery = true)
    List<User> getUserByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET NAME=?1, IBAN=?2, BIC=?3 WHERE EMAIL=?4", nativeQuery = true)
    Integer updateUser(String name, String iban, String bic, String email);
}
