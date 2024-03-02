package server.database;

import commons.Event;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for events.
 */
public interface EventRepository extends JpaRepository<Event, UUID> {}
