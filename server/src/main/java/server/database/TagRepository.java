package server.database;

import commons.User;
import commons.transactions.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * A repository for tags.
 */
public interface TagRepository extends JpaRepository<Tag, String> {
    @Query(value = "SELECT * FROM Tag t WHERE t.name = ?1", nativeQuery = true)
    List<Tag> findByName(String name);
}
