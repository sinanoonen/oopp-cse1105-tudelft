package commons;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Class for the composite key of User.
 */
@Embeddable
public class UserKey implements Serializable {
    private String email;
    private UUID eventID;

    /**
     * Empty constructor.
     */
    public UserKey() {

    }

    /**
     * Basic constructor.
     *
     * @param email the email of the user
     * @param eventID UUID of the event user is a part of
     */
    public UserKey(String email, UUID eventID) {
        this.email = email;
        this.eventID = eventID;
    }

    /**
     * Getter for email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for eventID.
     *
     * @return the eventID
     */
    public UUID getEventID() {
        return eventID;
    }

    /**
     * Setter for email.
     *
     * @param email the new email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the eventID.
     *
     * @param eventID eventID to set
     */
    public void setEventID(UUID eventID) {
        this.eventID = eventID;
    }

    /**
     * The default equals method.
     *
     * @param object the object to be compared to
     * @return true iff this and the object are equal
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof UserKey userKey)) {
            return false;
        }
        return Objects.equals(email, userKey.email)
            && Objects.equals(eventID, userKey.eventID);
    }

    /**
     * Generates a hashcode.
     *
     * @return the haschode
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, eventID);
    }
}
