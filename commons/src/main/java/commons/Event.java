package commons;

import commons.transactions.Expense;
import commons.transactions.Tag;
import commons.transactions.Transaction;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The Event class.
 */
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String inviteCode;
    private String title;
    @ManyToMany
    private List<User> participants;
    @OneToMany
    private List<Transaction> transactions;
    @OneToMany
    private Set<Tag> availableTags;

    /* unused */
    protected Event() {

    }

    /**
     * Constructor method.
     *
     * @param title of the event
     * @param creator of the event
     */
    public Event(String title, User creator) {
        this.title = title;
        this.participants = Collections.singletonList(creator);
        this.transactions = new ArrayList<>();
        this.availableTags = new HashSet<>(
                Arrays.asList(
                    new Tag("Food", new Color(147, 196, 125)),
                    new Tag("Entrance Fees", new Color(74, 134, 232)),
                    new Tag("Travel", new Color(224, 102, 102))
                )
        );
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
    public List<User> getParticipants() {
        return participants;
    }

    /**
     * Gets the total amount debt for a user in the event.
     *
     * @param user user whose debt to total.
     * @return total debt
     */
    public float getTotalDebt(User user) {
        return (float) getExpensesByParticipant(user.getName()).stream()
                .mapToDouble(expense -> expense.getDebts().get(user.getName()))
                .sum();
    }

    /**
     * Getter for the commons.transactions.
     *
     * @return commons.transactions
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
     * Adds a new tag that can be used for the commons.transactions of this event.
     *
     * @param tag tag to be added
     */
    public void addTag(Tag tag) {
        availableTags.add(tag);
    }

    /**
     * Getter for the tags of this event.
     *
     * @return availableTags
     */
    public Set<Tag> getTags() {
        return availableTags;
    }

    /**
     * Deletes a tag from the event.
     *
     * @param tag tag to be deleted
     * @return true if tag was successfully deleted, false otherwise
     */
    public boolean removeTag(Tag tag) {
        return availableTags.remove(tag);
    }

    /**
     * Adds a participant to the event.
     *
     * @param user user to be added to event
     * @return true if operation successful, false otherwise
     */
    public boolean addParticipant(User user) {
        return participants.add(user);
    }

    /**
     * Removes a participant from an event.
     *
     * @param user user to be removed from event
     * @return true if operation successful, false otherwise
     */
    public boolean removeParticipant(User user) {
        return participants.remove(user);
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
     * Gets a list of expenses by participant name.
     *
     * @param participant the participant name linked to the expenses
     * @return a list of expenses of the participant
     */
    public List<Expense> getExpensesByParticipant(String participant) {
        if (participants.stream()
                .map(user -> user.getName())
                .noneMatch(name -> name.equals(participant))
        ) {
            throw new IllegalArgumentException("Tag not found in Event");
        }

        return transactions.stream()
                .filter(transaction -> transaction instanceof Expense)
                .map(transaction -> (Expense) transaction)
                .filter(expense -> expense.getDebts().containsKey(participant))
                .toList();
    }

    /**
     * Gets a list of expenses by tag.
     *
     * @param tag the tag to filter by.
     * @return filtered list of expenses.
     */
    public List<Expense> getExpensesByTag(Tag tag) {
        if (!availableTags.contains(tag)) {
            throw new IllegalArgumentException("Tag not found in Event");
        }

        return transactions.stream()
                .filter(transaction -> transaction instanceof Expense)
                .map(transaction -> (Expense) transaction)
                .filter(expense -> expense.getTags().contains(tag))
                .toList();
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
                + ", commons.transactions="
                + transactions
                + '}';
    }
}
