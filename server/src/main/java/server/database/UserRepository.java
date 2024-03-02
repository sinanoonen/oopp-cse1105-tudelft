package server.database;

import commons.User;
import commons.UserKey;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for users.
 */
public interface UserRepository extends JpaRepository<User, UserKey> {}
