package server.database;

import commons.transactions.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository for tags.
 */
public interface TagRepository extends JpaRepository<Tag, String> {}
