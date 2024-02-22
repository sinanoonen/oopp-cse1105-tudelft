import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;

public class Event {
    private String inviteCode;
    private String title;
    private List<String> participants;
    private List<Transaction> transactions;

    public Event(String title, List<String> participants) {
        this.inviteCode = RandomStringUtils.randomAlphanumeric(10);
        this.title = title;
        this.participants = participants;
        this.transactions = new ArrayList<>();
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(inviteCode, event.inviteCode) && Objects.equals(title, event.title) && Objects.equals(participants, event.participants) && Objects.equals(transactions, event.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inviteCode, title, participants, transactions);
    }

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
