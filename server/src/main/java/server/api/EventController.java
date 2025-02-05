package server.api;

import commons.Currency;
import commons.Event;
import commons.User;
import commons.transactions.Expense;
import commons.transactions.Payment;
import commons.transactions.Tag;
import commons.transactions.Transaction;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import server.ListenerService;
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

    private final ListenerService listenerService;

    /**
     * Constructor for the event controller.
     *
     * @param repo event repository
     * @param exRepo expense repository
     * @param payRepo payment repository
     */
    public EventController(EventRepository repo,
                           ExpenseRepository exRepo,
                           PaymentRepository payRepo,
                           ListenerService listenerService) {
        this.repo = repo;
        this.exRepo = exRepo;
        this.payRepo = payRepo;
        this.listenerService = listenerService;
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
     * Poll events. Has a timeout of 5000ms (5s).
     *
     * @return the event
     */
    @GetMapping("/poll")
    public DeferredResult<ResponseEntity<String>> pollById() {
        final long timeoutTime = 5000; // 5000 ms == 5s
        ResponseEntity<String> noContent = ResponseEntity.noContent().build(); // Default return (on timeout)
        DeferredResult<ResponseEntity<String>> res = new DeferredResult<>(timeoutTime, noContent);

        Object key = new Object(); // Generate unique listener key
        listenerService.addListener(key, e -> {
            res.setResult(ResponseEntity.ok(e));
        });
        res.onCompletion(() -> {
            listenerService.removeListener(key);
        }); // Remove listener on completion (timeout/return)

        return res;
    }

    /**
     * Edit an event by its UUID.
     *
     * @param uuid the UUID of the event
     * @return the event
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<Event> updateById(@PathVariable("uuid") UUID uuid,
                                            @RequestBody Event updatedEvent) {
        if (!repo.existsById(uuid) || updatedEvent == null) {
            return ResponseEntity.badRequest().build();
        }
        Event saved = repo.save(updatedEvent);
        listenerService.notifyListeners(
            saved.getInviteCode().toString()); // Inform listeners that an event was updated
        return ResponseEntity.ok(saved);
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
        listenerService.notifyListeners(event.getInviteCode().toString());
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes an event by its uuid.
     *
     * @param uuid the uuid of the event to delete
     * @return ResponseEntity indicating the outcome of the operation
     */
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID uuid) {
        if (repo.existsById(uuid)) {
            repo.deleteById(uuid);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Returns the set of tags for an event.
     *
     * @param uuid uuid of event whose tags to fetch
     * @return null if event does not exist; set of tags otherwise
     */
    @GetMapping("/{uuid}/tags")
    public Set<Tag> getTags(@PathVariable("uuid") UUID uuid) {
        Optional<Event> event = repo.findById(uuid);
        return event.map(Event::getTags).orElse(null);
    }

    /**
     * Adds a tag to the provided event.
     *
     * @param uuid uuid of event to which to add a tag
     * @param tag tag to be added
     * @return modified event
     */
    @PostMapping("/{uuid}/tags")
    public ResponseEntity<Event> addTagToEvent(@PathVariable("uuid") UUID uuid, @RequestBody Tag tag) {
        Optional<Event> event = repo.findById(uuid);
        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        event.get().addTag(tag);
        Event saved = repo.save(event.get());
        listenerService.notifyListeners(saved.getInviteCode().toString());
        return ResponseEntity.ok(saved);
    }

    /**
     * Adds a user to an event.
     *
     * @param uuid the UUID of the event
     * @param user the user to add
     * @return the updated event
     */
    @PostMapping("/{uuid}/users")
    public ResponseEntity<Event> addUser(@PathVariable("uuid") UUID uuid, @RequestBody User user) {

        if (!repo.existsById(uuid)) {
            return ResponseEntity.badRequest().build();
        }

        Event event = repo.findById(uuid).get();
        event.addParticipant(user);
        try {
            Event saved = repo.save(event);
            listenerService.notifyListeners(saved.getInviteCode().toString());
            return ResponseEntity.ok(saved);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Removes a user from an event.
     *
     * @param uuid uuid of event from which to remove user
     * @param email email of user to remove
     * @return successful operation indicator
     */
    @DeleteMapping("/{uuid}/users/{email}")
    public ResponseEntity<Event> removeUserFromEvent(
            @PathVariable("uuid") UUID uuid, @PathVariable("email") String email) {
        if (uuid == null || isNullOrEmpty(uuid.toString()) || isNullOrEmpty(email)) {
            return ResponseEntity.badRequest().build();
        }
        if (!repo.existsById(uuid)) {
            return ResponseEntity.notFound().build();
        }
        Event event = repo.getReferenceById(uuid);
        Optional<User> toRemove = event.getParticipants()
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
        if (toRemove.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        event.removeParticipant(toRemove.get());
        Event saved = repo.save(event);
        listenerService.notifyListeners(saved.getInviteCode().toString());
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
     * Get all transactions belong to an event.
     *
     * @param uuid id of the event
     * @return list of transactions
     */
    @GetMapping("{uuid}/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactionsForEvent(@PathVariable("uuid") UUID uuid) {
        if (!repo.existsById(uuid)) {
            return ResponseEntity.badRequest().build();
        }
        Event event = repo.findById(uuid).get();
        List<Transaction> transactions = event.transactions();
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
        Optional<Transaction> transaction = event.transactions().stream()
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
    public ResponseEntity<Expense> addExpenseToEvent(
            @PathVariable("uuid") UUID uuid,
            @RequestBody Expense expense) {
        if (!repo.existsById(uuid)
                || isNullOrEmpty(expense.getOwner())
                || expense.getAmount() < 0
                || isNullOrEmpty(expense.getDescription())
        ) {
            return ResponseEntity.badRequest().build();
        }

        //expense.setDate(java.time.LocalDate.now());

        Event event = repo.findById(uuid).get();
        Expense saved = exRepo.save(expense);
        expense.setId(saved.getId());
        event.addTransaction(saved);
        Event newEvent = repo.save(event);

        listenerService.notifyListeners(newEvent.getInviteCode().toString());
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
    public ResponseEntity<Payment> addPaymentToEvent(
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
        listenerService.notifyListeners(event.getInviteCode().toString());
        return ResponseEntity.ok(saved);
    }

    /**
     * Removes transaction from an event.
     *
     * @param uuid id of the event
     * @param id id of the transaction
     * @return the event
     */
    @DeleteMapping("/{uuid}/transactions/{id}")
    public ResponseEntity<Event> removeTransactionFromEvent(
            @PathVariable("uuid") UUID uuid, @PathVariable("id") Long id) {
        if (uuid == null || isNullOrEmpty(uuid.toString()) || isNullOrEmpty(id.toString())) {
            return ResponseEntity.badRequest().build();
        }
        if (!repo.existsById(uuid)) {
            return ResponseEntity.notFound().build();
        }
        Event event = repo.getReferenceById(uuid);
        Optional<Transaction> toRemove = event.transactions().stream()
                .filter(e -> e.getId() == id)
                .findFirst();
        if (toRemove.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        event.removeTransaction(toRemove.get());
        Event saved = repo.save(event);

        Transaction transaction = toRemove.get();
        if (transaction instanceof Payment) {
            payRepo.deleteById(transaction.getId());
        } else if (transaction instanceof Expense) {
            exRepo.deleteById(transaction.getId());
        }

        listenerService.notifyListeners(saved.getInviteCode().toString());
        return ResponseEntity.ok(saved);
    }

    /**
     * Updates the expenses in the server.
     *
     * @param uuid id of the event
     * @param id id of the expense
     * @param update updated expense
     * @return updated expense
     */
    @PutMapping("/{uuid}/transactions/expenses/{id}")
    public ResponseEntity<Transaction> updateExpense(@PathVariable("uuid") UUID uuid, @PathVariable("id") Long id,
                                                  @RequestBody Expense update) {
        if (update == null) {
            update = new Expense(null, null, 0, null, null, null);
        }
        if (id == null || update.getId() != id) {
            System.out.println("Received bad PUT request");
            return ResponseEntity.badRequest().build();
        }
        var response = getTransactionByIdforEvent(uuid, id);
        if (response.getStatusCode().isError()) {
            System.out.println(" Received bad PUT request");
            return response;
        }
        System.out.println("Received valid PUT request");
        Expense expense = exRepo.findById(id).orElse(null);
        assert expense != null;
        //public Expense(String owner, LocalDate date, float amount, String description, List<String> participants
        String owner = isNullOrEmpty(update.getOwner()) ? expense.getOwner() : update.getOwner();
        LocalDate date = update.getDate() == null ? expense.getDate() : update.getDate();
        float amount = update.getAmount() == 0 ? expense.getAmount() : update.getAmount();
        Currency currency = update.getCurrency() == null ? expense.getCurrency() : update.getCurrency();
        String description = isNullOrEmpty(update.getDescription())
                ? expense.getDescription()
                : update.getDescription();
        Map<String, Float> debts = (update.getDebts().isEmpty() || update.getDebts() == null)
                ? expense.getDebts() : update.getDebts();
        expense.setSplitEqually(update.isSplitEqually());
        expense.setOwner(owner);
        expense.setDate(date);
        expense.setAmount(amount);
        expense.setCurrency(currency);
        expense.setDescription(description);
        expense.setDebts(debts);
        expense.setTags(update.getTags() == null ? expense.getTags() : update.getTags());
        Expense savedExpense = exRepo.save(expense);
        listenerService.notifyListeners(uuid.toString());
        return ResponseEntity.ok(savedExpense);
    }
}