package transactions;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

public abstract class Transaction {
    private String owner;
    private Date date;
    private float amount;

    public Transaction(String owner, Date date, float amount) {
        this.owner = owner;
        this.date = date;
        this.amount = Float.parseFloat(new DecimalFormat("#.##").format(amount));
    }

    public String getOwner() {
        return owner;
    }

    public Date getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "owner = '" + owner + '\'' +
                ", date = '" + date + '\'' +
                ", amount = " + amount +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Transaction that = (Transaction) object;
        return Float.compare(amount, that.amount) == 0 && Objects.equals(owner, that.owner) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, date, amount);
    }
}
