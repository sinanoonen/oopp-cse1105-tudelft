package commons;

import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Tag;
import commons.transactions.Transaction;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
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
    private List<Expense> expenses;
    @OneToMany
    private List<Payment> payments;
    @OneToMany (cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Tag> availableTags;

    @SuppressWarnings("unused")
    protected Event() {
        this.participants = new HashSet<>();
        this.expenses = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.availableTags = new HashSet<>();
    }

    /**
     * Constructor method.
     *
     * @param title of the event
     */
    public Event(String title) {
        this.inviteCode = UUID.randomUUID();
        this.title = title;
        this.participants = new HashSet<>();
        this.expenses = new ArrayList<>();
        this.payments = new ArrayList<>();
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
        this(title);
        participants.addAll(users);
    }

    public void setInviteCode(UUID uuid) {
        this.inviteCode = uuid;
    }

    public void setAvailableTags(Set<Tag> availableTags) {
        this.availableTags = availableTags;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    /**
     * Getter for the invite code.
     *
     * @return invite code
     */
    public UUID getInviteCode() {
        return inviteCode;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Payment> getPayments() {
        return payments;
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
    public List<Transaction> transactions() {
        List<Transaction> res = new ArrayList<>(expenses);
        res.addAll(payments);
        return res;
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
        boolean trash = transaction instanceof Expense
                ? expenses.add((Expense) transaction)
                : payments.add((Payment) transaction);
        return true;
    }

    /**
     * Removes a transaction from the event.
     *
     * @param transaction transaction to be removed
     * @return true if operation successful, false otherwise
     */
    public boolean removeTransaction(Transaction transaction) {
        return transaction instanceof Expense
                ? expenses.remove(transaction)
                : payments.remove(transaction);
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
        List<Transaction> transactions = transactions();

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

        return expenses.stream()
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

        return expenses.stream()
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
                && Objects.equals(transactions(), event.transactions());
    }

    /**
     * Hashcode for the event.
     *
     * @return integer hash of the event object
     */
    @Override
    public int hashCode() {
        return Objects.hash(inviteCode, title, participants, transactions());
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
                + transactions()
                + '}';
    }
}
