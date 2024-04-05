package commons.transactions;

import commons.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An abstract transaction class that represents any kind of transaction between the participants.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String owner;
    private LocalDate date;
    private float amount;
    private Currency currency;
    @ManyToMany
    private List<Tag> tags;

    protected Transaction() {
        this.tags = new ArrayList<>();
    }

    /**
     * Constructor for the transaction class.
     *
     * @param owner the owner of the transaction
     * @param date the date of the transaction
     * @param amount the amount of the transaction
     */
    public Transaction(String owner, LocalDate date, float amount, Currency currency) {
        this.owner = owner;
        this.date = date;
        this.amount = Float.parseFloat(new DecimalFormat("#.##").format(amount));
        this.tags = new ArrayList<>();
        this.currency = currency;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public long getId() {
        return id;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return id == that.id && Float.compare(amount, that.amount) == 0
                && Objects.equals(owner, that.owner)
                && Objects.equals(date, that.date)
                && currency == that.currency
                && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, date, amount, currency, tags);
    }
}
