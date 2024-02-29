package commons;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class for the composite key of User.
 */
@Embeddable
public class UserKey implements Serializable {
    private String email;
    private String iban;

    /**
     * Empty constructor.
     */
    public UserKey() {

    }

    /**
     * Basic constructor.
     *
     * @param email the email of the user
     * @param iban the iban of the user
     */
    public UserKey(String email, String iban) {
        this.email = email;
        this.iban = iban;
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
     * Getter for iban.
     *
     * @return the iban
     */
    public String getIban() {
        return iban;
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
     * Setter for iban.
     *
     * @param iban the new iban
     */
    public void setIban(String iban) {
        this.iban = iban;
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
            && Objects.equals(iban, userKey.iban);
    }

    /**
     * Generates a hashcode.
     *
     * @return the haschode
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, iban);
    }
}
