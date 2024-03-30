package algorithms;

import commons.Event;
import commons.User;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * The DebtSettler class.
 */
public class DebtSettler {
    private final Map<String, Float> debts;
    private final List<String> settledDebts;
    private final Event event;

    /**
     * Constructor method.
     *
     * @param event that needs the debts to be settled
     */
    public DebtSettler(Event event) {
        this.event = event;
        //Get all participants of an Event and their debts
        debts = new HashMap<>();
        for (User user : event.getParticipants()) {
            debts.put(user.getName(), event.getTotalDebt(user));
        }
        //Users will be ordered by the absolute value of their debt
        //Positive debt means someone owes another participant money
        //Negative debt means someone is owed money
        PriorityQueue<Map.Entry<String, Float>> positiveDebts = new PriorityQueue<>(
                Map.Entry.comparingByValue());
        PriorityQueue<Map.Entry<String, Float>> negativeDebts = new PriorityQueue<>(
                Comparator.comparing((Map.Entry<String, Float> entry) -> -entry.getValue()));
        //Put all users into the correct queue
        for (Map.Entry<String, Float> entry : debts.entrySet()) {
            if (entry.getValue() > 0) {
                positiveDebts.offer(entry);
            } else if (entry.getValue() < 0) {
                negativeDebts.offer(entry);
            }
        }
        settledDebts = new ArrayList<>();
        //The algorithm will match the users, and it settles the debts between all users
        while (!positiveDebts.isEmpty() && !negativeDebts.isEmpty()) {
            Map.Entry<String, Float> senderEntry = positiveDebts.poll();
            Map.Entry<String, Float> receiverEntry = negativeDebts.poll();
            String sender = senderEntry.getKey();
            assert receiverEntry != null;
            String receiver = receiverEntry.getKey();
            float senderDebt = senderEntry.getValue();
            float receiverDebt = receiverEntry.getValue();

            float transferAmount = Math.min(senderDebt, -receiverDebt);

            settledDebts.add(settleToString(transferAmount, sender, receiver));

            senderDebt -= transferAmount;
            receiverDebt += transferAmount;

            if (senderDebt > 0) {
                positiveDebts.offer(new AbstractMap.SimpleEntry<>(sender, senderDebt));
            }
            if (receiverDebt < 0) {
                negativeDebts.offer(new AbstractMap.SimpleEntry<>(receiver, receiverDebt));
            }
        }

        //Placeholders for when an error needs to occur as a result of something getting messed up with the debts.
        if (!positiveDebts.isEmpty() || !negativeDebts.isEmpty()) {
            if (!positiveDebts.isEmpty()) {
                if (Math.abs(positiveDebts.remove().getValue()) >= 0.01f) {
                    System.out.println("Debts cannot be settled completely.");
                }
            } else {
                if (Math.abs(negativeDebts.remove().getValue()) >= 0.01f) {
                    System.out.println("Debts cannot be settled completely.");
                } else {
                    System.out.println("All debts settles successfully.");
                }
            }
        } else {
            System.out.println("All debts settled successfully.");
        }
    }

    public Map<String, Float> getDebts() {
        return debts;
    }

    public List<String> getSettledDebts() {
        return settledDebts;
    }

    /**
     * Converts a debt payment to a string to be displayed on the debts page.
     *
     * @param transferAmount the amount of money that needs to be transferred from one person to the other
     * @param sender of the money
     * @param receiver of the money
     * @return a string representation of the debt payment
     */
    public String settleToString(float transferAmount, String sender, String receiver) {
        String result = sender + " should send " + transferAmount + " to " + receiver;
        User userSender = null;
        User userReceiver = null;
        for (User user : event.getParticipants()) {
            if (user.getName().equals(sender)) {
                userSender = user;
            }
            if (user.getName().equals(receiver)) {
                userReceiver = user;
            }
        }
        assert userSender != null;
        assert userReceiver != null;
        result += "\n\nYou can transfer the money to:\nIBAN: " + userReceiver.getIban()
                + "\nBIC: " + userReceiver.getBic()
                + "\n" + userReceiver.getName() + " can send a reminder to the E-mail: " + userSender.getEmail();
        return result;
    }
}
