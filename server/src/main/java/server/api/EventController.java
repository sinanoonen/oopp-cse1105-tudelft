package server.api;

import commons.Event;
import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.PaymentRepository;

/**
 * A controller for events.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository repo;

    private final ExpenseRepository exRepo;

    private final PaymentRepository payRepo;

    /**
     * Constructor for the event controller.
     *
     * @param repo event repository
     * @param exRepo expense repository
     * @param payRepo payment repository
     */
    public EventController(EventRepository repo, ExpenseRepository exRepo, PaymentRepository payRepo) {
        this.repo = repo;
        this.exRepo = exRepo;
        this.payRepo = payRepo;
    }

    /**
     * Get all events.
     *
     * @return all events
     */
    @GetMapping(path = { "", "/" })
    public List<Event> getAll() {
        return repo.findAll();
    }

    /**
     * Get an event by its UUID.
     *
     * @param uuid the UUID of the event
     * @return the event
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<Event> getById(@PathVariable("uuid") UUID uuid) {
        if (!repo.existsById(uuid)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(uuid).get());
    }

    /**
     * Add an event.
     *
     * @param event the event to add
     * @return the added event
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Event> add(@RequestBody Event event) {

        if (event.getInviteCode() == null || isNullOrEmpty(event.getTitle())
            || event.getTags() == null || event.getParticipants() == null
            || event.getTransactions() == null) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = repo.save(event);
        return ResponseEntity.ok(saved);
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param s the string s
     * @return true if the string is null or empty
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Get all the expenses belong to an event.
     *
     * @return list of all expenses
     */
    @SuppressWarnings({"checkstyle:WhitespaceAfter", "checkstyle:NoWhitespaceBefore"})
    @GetMapping(path = { "", "/{uuid}" ,"/transactions"})
    public ResponseEntity<List<Transaction>> getAllTransactionsForEvent(@PathVariable("uuid") UUID uuid) {
        if (!repo.existsById(uuid)) {
            return ResponseEntity.badRequest().build();
        }
        Event event = repo.findById(uuid).get();
        List<Transaction> transactions = event.getTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transaction belong to an event by id.
     *
     * @param uuid id of the event
     * @param trId id of the transaction
     * @return transaction
     */
    @GetMapping("/{uuid}/transactions/{trId}")
    public ResponseEntity<Transaction> getTransactionByIdforEvent(@PathVariable("uuid") UUID uuid,
                                                          @PathVariable("trId") Long trId) {
        if (!repo.existsById(uuid)) {
            return ResponseEntity.badRequest().build();
        }
        Event event = repo.findById(uuid).get();
        Optional<Transaction> transaction = event.getTransactions().stream()
                .filter(e -> e.getId() == trId)
                .findFirst();
        if (!transaction.isEmpty()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Adds a transaction to an event.
     *
     * @param uuid the  of the event
     * @param transaction expense to be added
     * @return the saved transaction
     */
    @PostMapping("/{uuid}/transactions")
    public ResponseEntity<Transaction> addTransactionToEvent(
            @PathVariable("uuid") UUID uuid,
            @RequestBody Transaction transaction) {
        if (!repo.existsById(uuid) || transaction.getAmount() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Event event = repo.findById(uuid).get();
        event.addTransaction(transaction);
        if (transaction instanceof Expense) {
            Expense expense = (Expense) transaction;
            Transaction savedExpense = exRepo.save(expense);
            return ResponseEntity.ok(savedExpense);
        } else {
            Payment payment = (Payment) transaction;
            Transaction savedPayment = payRepo.save(payment);
            return ResponseEntity.ok(savedPayment);
        }
    }



}