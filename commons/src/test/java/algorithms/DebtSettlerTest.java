package algorithms;

import commons.Event;
import commons.User;
import commons.transactions.Expense;
import commons.transactions.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DebtSettlerTest {

    Event event;
    @BeforeEach
    public void setupForTests() {
        Set<User> users = new HashSet<>();
        User dave = new User("Dave", "dave@gmail.com", "NL123456789", "bic1");
        User eva = new User("Eva", "eva@gmail.com", "NL987654321", "bic2");
        User mark = new User("Mark", "mark@gmail.com", "NL111122221", "bic3");
        User anne = new User("Anne", "anne@gmail.com", "NL444444444", "bic4");
        users.add(dave);
        users.add(eva);
        users.add(mark);
        users.add(anne);
        List<String> allParticipants = new ArrayList<>();
        allParticipants.add("Dave");
        allParticipants.add("Eva");
        allParticipants.add("Mark");
        allParticipants.add("Anne");
        List<String> allButMark = new ArrayList<>();
        allButMark.add("Dave");
        allButMark.add("Eva");
        allButMark.add("Anne");
        Expense expense1 = new Expense("Eva", LocalDate.of(2023, 7, 16),
                120.0f, "Train tickets", allParticipants);
        Expense expense2 = new Expense("Mark", LocalDate.of(2023, 7, 16),
                68.0f, "Lunch day 1", allParticipants);
        Expense expense3 = new Expense("Anne", LocalDate.of(2023, 7, 18),
                15.0f, "Beers", allButMark);
        Payment payment1 = new Payment("Dave", LocalDate.of(2023, 7, 19),
                20.0f, "Eva");
        event.addTransaction(expense1);
        event.addTransaction(expense2);
        event.addTransaction(expense3);
        event.addTransaction(payment1);
    }

    @Test
    public void DebtSettlerTests() {
        DebtSettler debtSettler = new DebtSettler(event);
        Map<String, Float> expectedDebts= new HashMap<>();
        //Do more stuff
        assertEquals(debtSettler.getDebts(), expectedDebts);
    }
}
