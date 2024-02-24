package transactions;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Expense class that extends from the transaction class.
 */
public class Expense extends Transaction {
    // Title of the expense
    private String description;
    // participants mapped to how much money they owe
    Map<String, Float> participants;

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
        this.participants = new HashMap<>();
        if (participants == null) {
            return;
        }
        participants.forEach(participant -> this.participants.put(participant, 0f));
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
     * Getter for description.
     *
     * @return description
     */
    public Map<String, Float> getParticipants() {
        return participants;
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
     * adds a participant to the expense.
     *
     * @param participant participant to be added
     * @return true if participant successfully added, false otherwise
     */
    public boolean addParticipant(String participant) {
        if (participants.containsKey(participant)) {
            return false;
        }
        participants.put(participant, 0f);
        return true;
    }

    /**
     * Updates the debt of a participant for this expense.
     *
     * @param participant participant whose debt should be changed
     * @param change change in debt (+ => more debt)
     * @return true if operation performed successfully, false otherwise
     */
    public boolean modifyParticipant(String participant, float change) {
        if (!participants.containsKey(participant)) {
            return false;
        }
        // This assumes that values > 0 represent debt
        float newValue = participants.get(participant) + change;
        participants.put(participant, newValue);
        return true;
    }

    /**
     * Removes participant from expense
     * and splits their debt evenly among others.
     *
     * @param participant participant to be removed
     * @return true if operation performed successfully, false otherwise
     */
    public boolean removeParticipant(String participant) {
        if (!participants.containsKey(participant)) {
            return false;
        }
        float amountToSplit = participants.remove(participant);
        return splitEqually(amountToSplit);
    }

    /**
     * Splits the total value of the expense equally among all participants of the event.
     * @return true if operation was successful, false otherwise
     */
    public boolean splitEqually(float amount) {
        List<String> allParticipants = getParticipants().keySet().stream().toList();
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
     * @param amount amount that should be split among the subgroup.
     * @param userMultiplierMap a map containing all users that should pay, mapped to a multiplier
     * @return true if successful operation, false otherwise.
     */
    public boolean splitAmong(float amount, Map<String, Integer> userMultiplierMap) {
        int numSplits = userMultiplierMap.values().stream().mapToInt(Integer::intValue).sum();
        float splitAmount = amount / numSplits;
        // round to 2 dp
        splitAmount = Float.parseFloat(new DecimalFormat("#.##").format(splitAmount));
        float remainder = amount - (userMultiplierMap.size() * splitAmount);
        for (Map.Entry<String, Integer> entry : userMultiplierMap.entrySet()) {
            String user = entry.getKey();
            int multiplier = entry.getValue();
            float change = splitAmount * multiplier + remainder > 0 ? 0.01f : 0;
            remainder -= 0.01f;
            if (!modifyParticipant(user, change)) {
                return false;
            }
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
                + "description='"
                + description + '\''
                + ", participants="
                + participants
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
        return Objects.equals(participants, expense.participants);
    }

    /**
     * Hashcode for the expense.
     *
     * @return integer hash of the expense object
     */
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
        return result;
    }
}
