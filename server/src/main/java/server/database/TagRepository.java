package server.database;


import commons.transactions.Tag;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;



/**
 * A repository for tags.
 */
public interface TagRepository extends JpaRepository<Tag, String> {
    @Query(value = "SELECT * FROM Tag t WHERE t.name = ?1", nativeQuery = true)
    List<Tag> findByName(String name);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Tag SET COLOR=?1 WHERE NAME=?2", nativeQuery = true)
    int updateTagColor(int color, String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Tag WHERE name=?1", nativeQuery = true)
    Integer deleteTagByName(String name);
}
