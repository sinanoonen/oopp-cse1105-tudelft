package commons;

import commons.transactions.Expense;
import commons.transactions.Payment;
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
import java.util.UUID;

/**
 * The Event class.
 */
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID inviteCode;
    private String title;
    @ManyToMany
    private Set<User> participants;
    @OneToMany
    private List<Transaction> transactions;
    @OneToMany
    private Set<Tag> availableTags;

    @SuppressWarnings("unused")
    protected Event() {

    }

    /**
     * Constructor method.
     *
     * @param title of the event
     * @param creator of the event
     */
    public Event(String title, User creator) {
        this.inviteCode = UUID.randomUUID();
        this.title = title;
        this.participants = new HashSet<>(Collections.singletonList(creator));
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
     * Constructor method.
     *
     * @param title of the event
     * @param users that partake in the event
     */
    public Event(String title, Set<User> users) {
        this(title, users.iterator().next());
        participants.addAll(users);
    }

    /**
     * Getter for the invite code.
     *
     * @return invite code
     */
    public UUID getInviteCode() {
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
    public Set<User> getParticipants() {
        return participants;
    }

    /**
     * Gets the total amount debt for a user in the event.
     *
     * @param user user whose debt to total.
     * @return total debt
     */
    public float getTotalDebt(User user) {
        float expenseDebt = (float) getExpensesByParticipant(user.getName()).stream()
                .mapToDouble(expense -> expense.getDebts().get(user.getName()))
                .sum();
        float paymentDebt = (float) getPaymentsByParticipant(user.getName()).stream()
                .mapToDouble(payment -> payment.getDebt().get(user.getName()))
                .sum();
        return expenseDebt + paymentDebt;
    }

    /**
     * Getter for the transactions.
     *
     * @return commons.transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Setter for the title.
     *
     * @param title title of the event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    //    /**
    //     * Setter for invite code.
    //     *
    //     * @param code UUID to set inviteCode
    //     */
    //    public void setInviteCode(UUID code) {
    //        this.inviteCode = code;
    //    }

    /**
     * Adds a new tag that can be used for the transactions of this event.
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
     * @throws IllegalArgumentException if the tag is not found.
     */
    public boolean removeTag(Tag tag) {
        if (!this.availableTags.contains(tag)) {
            throw new IllegalArgumentException("Tag not found");
        }
        return availableTags.remove(tag);
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
     * Gets a list of payments by participant name.
     *
     * @param participant the participant name linked to the payments
     * @return a list of payments of the participant
     */
    private List<Payment> getPaymentsByParticipant(String participant) {
        if (participants.stream()
                .map(User::getName)
                .noneMatch(name -> name.equals(participant))
        ) {
            throw new IllegalArgumentException("Participant not found in Event");
        }

        return transactions.stream()
                .filter(transaction -> transaction instanceof Payment)
                .map(transaction -> (Payment) transaction)
                .filter(payment -> payment.getDebt().containsKey(participant))
                .toList();
    }

    /**
     * Gets a list of expenses by participant name.
     *
     * @param participant the participant name linked to the expenses
     * @return a list of expenses of the participant
     */
    public List<Expense> getExpensesByParticipant(String participant) {
        if (participants.stream()
                .map(User::getName)
                .noneMatch(name -> name.equals(participant))
        ) {
            throw new IllegalArgumentException("Participant not found in Event");
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
