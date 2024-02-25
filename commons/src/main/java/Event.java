import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;
import transactions.Transaction;

/**
 * The Event class.
 */
public class Event {
    private String inviteCode;
    private String title;
    private Map<User, Float> participants;
    private List<Transaction> transactions;

    /**
     * Constructor method.
     *
     * @param title of the event
     * @param creator of the event
     */
    public Event(String title, User creator) {
        this.inviteCode = RandomStringUtils.randomAlphanumeric(10);
        this.title = title;
        this.participants = new HashMap<>();
        // Creator of the event will automatically be a participant
        participants.put(creator, 0f);
        this.transactions = new ArrayList<>();
    }

    /**
     * Getter for the invite code.
     *
     * @return invite code
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Getter for the title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the participants.
     *
     * @return participants
     */
    public Map<User, Float> getParticipants() {
        return participants;
    }

    /**
     * Getter for the transactions.
     *
     * @return transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Setter for the invite code.
     *
     * @param inviteCode invite code of the event
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    /**
     * Setter for the title.
     *
     * @param title title of the event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Adds a participant to the event.
     *
     * @param user user to be added to event
     * @return true if operation successful, false otherwise
     */
    public boolean addParticipant(User user) {
        if (user == null) {
            return false;
        }
        participants.put(user, 0f);
        return true;
    }

    /**
     * Removes a participant from an event.
     *
     * @param user user to be removed from event
     * @return true if operation successful, false otherwise
     */
    public boolean removeParticipant(User user) {
        if (!participants.containsKey(user)) {
            return false;
        }
        participants.remove(user);
        return true;
    }

    /**
     * Adds a transaction to the event.
     *
     * @param transaction transaction to be added
     * @return true if operation successful, false otherwise
     */
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        transactions.add(transaction);
        return true;
    }

    /**
     * Removes a transaction from the event.
     *
     * @param transaction transaction to be removed
     * @return true if operation successful, false otherwise
     */
    public boolean removeTransaction(Transaction transaction) {
        return transactions.remove(transaction);
    }

    /**
     * Equals method of the event.
     *
     * @param o object to compare to
     * @return true or false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(inviteCode, event.inviteCode)
                && Objects.equals(title, event.title)
                && Objects.equals(participants, event.participants)
                && Objects.equals(transactions, event.transactions);
    }

    /**
     * Hashcode for the event.
     *
     * @return integer hash of the event object
     */
    @Override
    public int hashCode() {
        return Objects.hash(inviteCode, title, participants, transactions);
    }

    /**
     * toString of the event method.
     *
     * @return string representing the event object
     */
    @Override
    public String toString() {
        return "Event{"
                + "inviteCode='"
                + inviteCode
                + '\''
                + ", title='"
                + title
                + '\''
                + ", participants="
                + participants
                + ", transactions="
                + transactions
                + '}';
    }
}
