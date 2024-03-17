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
    @Query(value = "SELECT * FROM Tag t WHERE t.id = ?1", nativeQuery = true)
    List<Tag> findById(long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Tag SET COLOR=?2, NAME=?3  WHERE ID=?1", nativeQuery = true)
    int updateTag(long id, int color, String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Tag WHERE id=?1", nativeQuery = true)
    Integer deleteTagByName(long id);
}
