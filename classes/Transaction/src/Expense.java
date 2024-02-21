import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Expense extends Transaction{
    // Title of the expense
    private String description;
    // participants mapped to how much money they owe
    Map<String, Float> participants;

    /**
     * Constructor method
     * @param owner who paid the expense
     * @param date when was the expense paid
     * @param amount how much was paid
     * @param description short description of what the expense was
     * @param participants list containing initial participants of expense
     */
    public Expense(String owner, String date, float amount, String description, List<String> participants) {
        super(owner, date, amount);
        this.description = description;
        this.participants = new HashMap<>();
        if(participants == null)
            return;
        participants.forEach(participant -> {this.participants.put(participant, 0f);});
    }

    /**
     * Getter for description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Updates the debt of a participant for this expense
     * @param participant participant whose debt should be changed
     * @param change change in debt (+ => more debt)
     * @return true if operation performed successfully, false otherwise
     */
    public boolean modifyParticipant(String participant, float change) {
        if(!participants.containsKey(participant))
            return false;
        // This assumes that values > 0 represent debt
        float newValue = participants.get(participant) + change;
        participants.put(participant, newValue);
        return true;
    }

    /**
     * Removes participant from expense,
     * and splits their debt evenly among others
     * @param participant participant to be removed
     * @return true if operation performed successfully, false otherwise
     */
    public boolean removeParticipant(String participant) {
        if(!participants.containsKey(participant))
            return false;
        float amountToSplit = participants.remove(participant);
        float splitAmount = amountToSplit / participants.size();
        // round to 2 dp
        splitAmount = Float.parseFloat(new DecimalFormat("#.##").format(splitAmount));
        float remainder = amountToSplit - (participants.size() * splitAmount);
        for(String p : participants.keySet()) {
            // If there is a remainder of n cents, the first n people pay 1 cent extra
            float change = splitAmount + remainder > 0 ? 0.01f : 0;
            remainder -= 0.01f;
            if(!modifyParticipant(p, change)) // check successful modification
                return false;
        }
        return true;
    }
}
