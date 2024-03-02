package server.database;

import commons.User;
import commons.UserKey;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Repository for users.
 */
public interface UserRepository extends JpaRepository<User, UserKey> {
    @Query(value = "SELECT * FROM USERS u WHERE u.EMAIL=?1", nativeQuery = true)
    List<User> getUserByEmail(String email);
}
