import java.util.Objects;

public class User {
    private String name;
    private String email;
    private String IBAN;
    private String BIC;

    /**
     * Constructor for the user class.
     *
     * @param name name of the user which will serve as the username
     * @param email email of the user
     * @param IBAN IBAN of the user
     * @param BIC BIC of the user
     */
    public User(String name, String email, String IBAN, String BIC) {
        this.name = name;
        this.email = email;
        this.IBAN = IBAN;
        this.BIC = BIC;
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
    public String getIBAN() {
        return IBAN;
    }

    /**
     * Getter for the BIC of the user.
     *
     * @return the BIC of the user
     */
    public String getBIC() {
        return BIC;
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
     * @param IBAN new iban of the user.
     */
    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    /**
     * Setter for the BIC.
     * @param BIC new BIC of the user.
     */
    public void setBIC(String BIC) {
        this.BIC = BIC;
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
                + IBAN
                + '\''
                + ", BIC='"
                + BIC
                + '\''
                + '}';
    }
    /**
     * Equals method for the user class
     *
     * @param o object to be compared
     * @return true if the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(IBAN, user.IBAN) && Objects.equals(BIC, user.BIC);
    }

    /**
     * Hashcode for the user.
     *
     * @return integer hashcode for the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, email, IBAN, BIC);
    }
}
