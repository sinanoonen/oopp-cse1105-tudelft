import java.util.Objects;

public class Participant {
    private String name;
    private float debt;

    /**
     * A basic constructor for a participant
     *
     * @param name the name of the participant
     * @param debt the debt of the participant
     */
    public Participant(String name, float debt) {
        this.name = name;
        this.debt = debt;
    }

    /**
     * Creates a participant with debt 0 initially
     *
     * @param name the name of the participant
     */
    public Participant(String name) {
        this.name = name;
        this.debt = 0;
    }

    /**
     * Getter for the name
     *
     * @return the name of the participant
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the debt
     *
     * @return the debt of the participant
     */
    public float getDebt() {
        return debt;
    }

    /**
     * Sets the name of the participant
     *
     * @param name the new name of the participant
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the debt of the participant
     *
     * @param debt the new debt of the participant
     */
    public void setDebt(float debt) {
        this.debt = debt;
    }

    /**
     * A basic equals method
     *
     * @param object the object to be compared to
     * @return true iff this and the object are equal
     */
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Participant)) return false;
        Participant that = (Participant) object;
        return java.lang.Float.compare(debt, that.debt) == 0 &&
            java.util.Objects.equals(name, that.name);
    }

    /**
     * Generates a hashcode for the participant
     *
     * @return the hashcode
     */
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, debt);
    }

    /**
     * Generates a string representation of the participant
     *
     * @return the string representation
     */
    public String toString() {
        return "Participant{"
            + "name='" + name + '\''
            + ", debt=" + debt
            + '}';
    }
}