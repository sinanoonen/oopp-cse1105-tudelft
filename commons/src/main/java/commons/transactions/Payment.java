package commons.transactions;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The payment class that extends from the transaction class.
 */
@Entity
public class Payment extends Transaction {
    private String recipient;
    private String sender;
    Map<String, Float> debt;

    @SuppressWarnings("unused")
    protected Payment() {
        this.setOwner(this.sender);
    }

    /**
     * Constructor for the payment class.
     *
     * @param date the date of the transaction
     * @param amount the amount of the transaction
     * @param recipient the recipient of the payment
     * @param sender the sender of the payment
     */
    public Payment(LocalDate date, float amount, String recipient, String sender) {
        super(sender, date, amount);
        this.recipient = recipient;
        this.sender = sender;
        debtPutter(sender, recipient, amount);
    }

    /**
     * The owner of the transaction is also the sender of the payment.
     *
     * @param owner the owner of the transaction
     * @param date the date of the transaction
     * @param amount the amount of the transaction
     * @param recipient the recipient of the payment
     */
    public Payment(String owner, LocalDate date, float amount, String recipient) {
        super(owner, date, amount);
        this.recipient = recipient;
        this.sender = owner;
        debtPutter(owner, recipient, amount);
    }

    /**
     * Updates the debts of the people involved in the payment.
     *
     * @param sender the sender of the payment
     * @param recipient the recipient of the payment
     * @param amount the amount of money in the payment
     */
    private void debtPutter(String sender, String recipient, float amount) {
        debt = new HashMap<>();
        debt.put(sender, amount * -1);
        debt.put(recipient, amount);
    }

    /**
     * Getter for the sender.
     *
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Setter for the sender.
     *
     * @param sender the new sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Getter for the recipient.
     *
     * @return the recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Setter for the recipient.
     *
     * @param recipient the new recipient
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Getter for debt map.
     *
     * @return debt
     */
    public Map<String, Float> getDebt() {
        return debt;
    }

    /**
     * Generates a string representation of the payment.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Payment{" + super.toString()
            + "sender='" + sender + '\''
            + ", recipient='" + recipient + '\''
            + '}';
    }

    /**
     * The equals method.
     *
     * @param object the object to be compared to
     * @return true iff the two objects are equal
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!object.getClass().equals(getClass())) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        Payment that = (Payment) object;

        return Objects.equals(sender, that.sender)
            && Objects.equals(recipient, that.recipient);
    }

    /**
     * Generates a hashcode.
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sender, recipient);
    }
}