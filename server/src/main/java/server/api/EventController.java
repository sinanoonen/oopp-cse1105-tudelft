package server.api;

import commons.Event;
import commons.User;
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
import org.springframework.web.bind.annotation.PutMapping;
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

        if (event == null || isNullOrEmpty(event.getTitle())) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = repo.save(event);
        return ResponseEntity.ok(saved);
    }


    /**
     * Adds a user to an event.
     *
     * @param uuid the UUID of the event
     * @param user the user to add
     * @return the updated event
     */
    @PutMapping("/{uuid}/users")
    public ResponseEntity<Event> addUser(@PathVariable("uuid") UUID uuid, @RequestBody User user) {
        // TODO should this be a put or a post?
        if (!repo.existsById(uuid)) {
            return ResponseEntity.badRequest().build();
        }

        Event event = repo.findById(uuid).get();
        event.addParticipant(user);
        repo.save(event);
        return ResponseEntity.ok(event);
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
    @GetMapping("{uuid}/transactions")
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
     * @param uuid the UUID of the event
     * @param expense expense to be added
     * @return the saved transaction
     */
    @PostMapping("/{uuid}/transactions/expenses")
    public ResponseEntity<Expense> addTransactionToEvent(
            @PathVariable("uuid") UUID uuid,
            @RequestBody Expense expense) {
        if (!repo.existsById(uuid)
                || isNullOrEmpty(expense.getOwner())
                || expense.getAmount() < 0
                || isNullOrEmpty(expense.getDescription())
        ) {
            return ResponseEntity.badRequest().build();
        }

        expense.setDate(java.time.LocalDate.now());

        Event event = repo.findById(uuid).get();
        event.addTransaction(expense);
        Expense saved = exRepo.save(expense);
        return ResponseEntity.ok(saved);
    }

    /**
     * Adds a transaction to an event.
     *
     * @param uuid the UUID of the event
     * @param payment payment to be added
     * @return the saved transaction
     */
    @PostMapping("/{uuid}/transactions/payments")
    public ResponseEntity<Payment> addTransactionToEvent(
            @PathVariable("uuid") UUID uuid,
            @RequestBody Payment payment) {
        if (!repo.existsById(uuid)
                || isNullOrEmpty(payment.getSender())
                || payment.getAmount() <= 0
                || isNullOrEmpty(payment.getRecipient())
        ) {
            return ResponseEntity.badRequest().build();
        }

        payment.setDate(java.time.LocalDate.now());

        Event event = repo.findById(uuid).get();
        event.addTransaction(payment);
        Payment saved = payRepo.save(payment);
        return ResponseEntity.ok(saved);
    }
}