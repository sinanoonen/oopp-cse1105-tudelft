package transactions;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Objects;

/**
 * An abstract transaction class that represents any kind of transaction between the participants.
 */
public abstract class Transaction {
    private String owner;
    private LocalDate date;
    private float amount;

    /**
     * Constructor for the transaction class.
     *
     * @param owner the owner of the transaction
     * @param date the date of the transaction
     * @param amount the amount of the transaction
     */
    public Transaction(String owner, LocalDate date, float amount) {
        this.owner = owner;
        this.date = date;
        this.amount = Float.parseFloat(new DecimalFormat("#.##").format(amount));
    }

    public String getOwner() {
        return owner;
    }

    public LocalDate getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{"
                + "owner = '"
                + owner
                + '\''
                + ", date = '"
                + date + '\''
                + ", amount = "
                + amount
                + '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Transaction that = (Transaction) object;
        return Float.compare(amount, that.amount) == 0 && Objects.equals(owner, that.owner)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, date, amount);
    }
}
