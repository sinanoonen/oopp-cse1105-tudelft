import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class Expense extends Transaction{
    // Title of the expense
    private String name;
    // participants mapped to how much money they owe
    Map<String, Float> participants;

    public Expense(String owner, String date, float amount, String name, List<String> participants) {
        super(owner, date, amount);
        this.name = name;
        if(participants == null)
            return;
        participants.forEach(participant -> {this.participants.put(participant, 0f);});
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean removeParticipant(String participant) {
        if(!participants.containsKey(participant))
            return false;
        float amountToSplit = participants.remove(participant);
        float splitAmount = amountToSplit / participants.size();
        // round to 2 dp
        splitAmount = Float.parseFloat(new DecimalFormat("#.##").format(splitAmount));
        float remainder = amountToSplit - (participants.size() * splitAmount);
        for(String p : participants.keySet()) {
            float v = participants.get(p);
            // This assumes that values > 0 represent debt
            // If there is a remainder of n cents, the first n people pay 1 cent extra
            float newValue = v + splitAmount + remainder > 0 ? 0.01f : 0;
            remainder -= 0.01f;
            participants.put(p, newValue);
        }
        return true;
    }
}
