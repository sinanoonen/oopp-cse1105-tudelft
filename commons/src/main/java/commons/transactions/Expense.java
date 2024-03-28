package commons.transactions;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Expense class that extends from the transaction class.
 */
@Entity
@Table(name = "expenses")
public class Expense extends Transaction {
    // Title of the expense
    private String description;
    // a map of each participant's debt within this expense
    @ElementCollection
    Map<String, Float> debts;

    private boolean splitEqually = false;

    @SuppressWarnings("unused")
    protected Expense() {
        this.debts = new HashMap<>();
    }

    /**
     * Constructor method.
     *
     * @param owner who paid the expense
     * @param date when was the expense paid
     * @param amount how much was paid
     * @param description short description of what the expense was
     * @param participants list containing initial participants of expense
     */
    public Expense(String owner, LocalDate date, float amount, String description, List<String> participants) {
        super(owner, date, amount);
        this.description = description;
        this.debts = new HashMap<>();
        if (participants == null) {
            return;
        }
        participants.forEach(participant -> this.debts.put(participant, 0f));
        splitEqually(amount);
        if (debts.get(owner) != null) {
            debts.put(owner, ((debts.get(owner)) - amount));
        } else {
            debts.put(owner, -1 * amount);
        }
    }

    /**
     * Constructor with custom multiplier map.
     *
     * @param owner who paid the expense
     * @param date when was the expense paid
     * @param amount how much was paid
     * @param description short description of what the expense was
     * @param participants list containing initial participants of expense
     * @param multiplier map containing how should the amount be split
     */
    public Expense(String owner, LocalDate date, float amount, String description, List<String> participants,
                   Map<String, Integer> multiplier) {
        super(owner, date, amount);
        this.description = description;
        this.debts = new HashMap<>();
        if (participants == null) {
            return;
        }
        participants.forEach(participant -> this.debts.put(participant, 0f));
        splitAmong(amount, multiplier);
        if (debts.get(owner) != null) {
            debts.put(owner, ((debts.get(owner)) - amount));
        } else {
            debts.put(owner, -1 * amount);
        }
    }

    public boolean isSplitEqually() {
        return splitEqually;
    }

    public void setSplitEqually(boolean splitEqually) {
        this.splitEqually = splitEqually;
    }

    /**
     * Getter for description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for debts map.
     *
     * @return debts
     */
    public Map<String, Float> getDebts() {
        return debts;
    }

    public void setDebts(Map<String, Float> debts) {
        this.debts = debts == null ? new HashMap<>() : debts;
    }

    /**
     * Setter for description.
     *
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Override transaction setter to update debts after changing expense amount.
     *
     * @param amount new amount.
     */
    @Override
    public void setAmount(float amount) {
        super.setAmount((amount));
        debts.put(getOwner(), -1 * amount);
        splitEqually(amount);
    }

    @Override
    public void setOwner(String owner) {
        debts.remove(getOwner());
        super.setOwner(owner);
    }

    /**
     * Splits the total value of the expense equally among all participants of the event.
     *
     * @return true if operation was successful, false otherwise
     */
    public boolean splitEqually(float amount) {
        List<String> allParticipants = new java.util.ArrayList<>(debts.keySet().stream().toList());
        allParticipants.removeIf(participant -> debts.get(participant) < 0);
        Map<String, Integer> usersMultiplierMap = new HashMap<>();
        allParticipants.forEach(p -> {
            usersMultiplierMap.put(p, 1);
        });
        return splitAmong(amount, usersMultiplierMap);
    }

    /**
     * Splits an amount among a subgroup of users,
     * where each user has a multiple expressing
     * what fraction of the expense they should pay.
     *
     * @param amount amount that should be split among the subgroup.
     * @param userMultiplierMap a map containing all users that should pay, mapped to a multiplier
     * @return true if successful operation, false otherwise.
     */
    public boolean splitAmong(float amount, Map<String, Integer> userMultiplierMap) {
        super.setAmount(amount);
        int splits = 0;
        for (Map.Entry<String, Integer> entry : userMultiplierMap.entrySet()) {
            splits = splits + entry.getValue();
        }
        float oneAmount = splits == 0 ? amount : amount / splits;
        oneAmount = Float.parseFloat(new DecimalFormat("#.##").format(oneAmount));
        for (Map.Entry<String, Integer> entry : userMultiplierMap.entrySet()) {
            String user = entry.getKey();
            int multiplier = entry.getValue();
            debts.put(user, multiplier * oneAmount);
        }
        return true;
    }

    /**
     * toString of the expense method.
     *
     * @return string representing the expense object
     */
    @Override
    public java.lang.String toString() {
        return "Expense{" + super.toString()
                + ", description='"
                + description + '\''
                + ", debts="
                + debts
                + '}';
    }

    /**
     * Equals method of the expense.
     *
     * @param object object to compare to
     * @return true or false
     */
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        Expense expense = (Expense) object;

        if (!Objects.equals(description, expense.description)) {
            return false;
        }
        return Objects.equals(debts, expense.debts);
    }

    /**
     * Hashcode for the expense.
     *
     * @return integer hash of the expense object
     */
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (debts != null ? debts.hashCode() : 0);
        return result;
    }
}
