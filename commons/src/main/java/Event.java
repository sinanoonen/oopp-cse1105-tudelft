import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import transactions.Transaction;
import org.apache.commons.lang3.RandomStringUtils;

public class Event {
    private String inviteCode;
    private String title;
    private List<String> participants;
    private List<Transaction> transactions;

    /**
     * Constructor method
     * @param title of the event
     * @param participants of the event
     */
    public Event(String title, List<String> participants) {
        this.inviteCode = RandomStringUtils.randomAlphanumeric(10);
        this.title = title;
        this.participants = participants;
        this.transactions = new ArrayList<>();
    }

    /**
     * Getter for the invite code
     * @return invite code
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Getter for the title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the participants
     * @return participants
     */
    public List<String> getParticipants() {
        return participants;
    }

    /**
     * Getter for the transactions
     * @return transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Setter for the invite code
     * @param inviteCode
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    /**
     * Setter for the title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Setter for the participants
     * @param participants
     */
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    /**
     * Setter for the transactions
     * @param transactions
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Equals method of the event
     * @param o object to compare to
     * @return true or false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(inviteCode, event.inviteCode) && Objects.equals(title, event.title) && Objects.equals(participants, event.participants) && Objects.equals(transactions, event.transactions);
    }

    /**
     * Hashcode for the event
     * @return integer hash of the event object
     */
    @Override
    public int hashCode() {
        return Objects.hash(inviteCode, title, participants, transactions);
    }

    /**
     * toString of the event method
     * @return string representing the event object
     */
    @Override
    public String toString() {
        return "Event{" +
                "inviteCode='" + inviteCode + '\'' +
                ", title='" + title + '\'' +
                ", participants=" + participants +
                ", transactions=" + transactions +
                '}';
    }
}
