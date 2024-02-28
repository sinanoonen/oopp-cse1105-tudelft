package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

/**
 * User class.
 */
@Entity
public class User {
    private String name;
    @Id
    private String email;
    @Id
    private String iban;
    private String bic;

    /* unused */
    protected User() {
        
    }

    /**
     * Constructor for the user class.
     *
     * @param name name of the user which will serve as the username
     * @param email email of the user
     * @param iban IBAN of the user
     * @param bic BIC of the user
     */
    public User(String name, String email, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    /**
     * Getter method for the name.
     *
     * @return name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the email of the user.
     *
     * @return email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for the IBAN of the user.
     *
     * @return the iban of the user
     */
    public String getIban() {
        return iban;
    }

    /**
     * Getter for the BIC of the user.
     *
     * @return the BIC of the user
     */
    public String getBic() {
        return bic;
    }

    /**
     * Setter for the name.
     *
     * @param name new name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the email.
     *
     * @param email new email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter for the IBAN.
     *
     * @param iban new iban of the user.
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Setter for the BIC.
     *
     * @param bic new BIC of the user.
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * Generates string representation of the user.
     *
     * @return the string representation of the user
     */
    @Override
    public String toString() {
        return "User{"
                + "name='"
                + name + '\''
                + ", email='"
                + email
                + '\''
                + ", IBAN='"
                + iban
                + '\''
                + ", BIC='"
                + bic
                + '\''
                + '}';
    }
    /**
     * Equals method for the user class.
     *
     * @param o object to be compared
     * @return true if the two objects are equal
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(email, user.email)
                && Objects.equals(iban, user.iban) && Objects.equals(bic, user.bic);
    }

    /**
     * Hashcode for the user.
     *
     * @return integer hashcode for the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, email, iban, bic);
    }
}
