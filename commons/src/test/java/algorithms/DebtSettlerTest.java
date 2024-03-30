package algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import commons.Event;
import commons.User;
import commons.transactions.Expense;
import commons.transactions.Payment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test for the DebtSettler class.
 */
public class DebtSettlerTest {

    private Event event;

    /**
     * Code to be executed before each test.
     */
    @BeforeEach
    public void setupForTests() {
        Set<User> users = new HashSet<>();
        UUID id = UUID.randomUUID();
        User dave = new User("Dave", "dave@gmail.com", "NL123456789", "bic1", id);
        User eva = new User("Eva", "eva@gmail.com", "NL987654321", "bic2", id);
        User mark = new User("Mark", "mark@gmail.com", "NL111122221", "bic3", id);
        User anne = new User("Anne", "anne@gmail.com", "NL444444444", "bic4", id);
        users.add(dave);
        users.add(eva);
        users.add(mark);
        users.add(anne);
        event = new Event("Vacation", users);
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
    public void debtSettlerTests() {
        DebtSettler debtSettler = new DebtSettler(event);
        Map<String, Float> expectedDebts = getStringFloatMap();
        assertEquals(expectedDebts, debtSettler.getDebts());


        List<String> expectedSettledDebts = new ArrayList<>();
        expectedSettledDebts.add("""
                Dave should send 21.0 to Mark
                
                You can transfer the money to:
                IBAN: NL111122221
                BIC: bic3
                Mark can send a reminder to the E-mail: dave@gmail.com""");
        expectedSettledDebts.add("""
                Dave should send 11.0 to Eva
                
                You can transfer the money to:
                IBAN: NL987654321
                BIC: bic2
                Eva can send a reminder to the E-mail: dave@gmail.com""");
        expectedSettledDebts.add("""
                Anne should send 37.0 to Eva
                
                You can transfer the money to:
                IBAN: NL987654321
                BIC: bic2
                Eva can send a reminder to the E-mail: anne@gmail.com""");
        assertEquals(expectedSettledDebts, debtSettler.getSettledDebts());
    }

    private static Map<String, Float> getStringFloatMap() {
        Map<String, Float> expectedDebts = new HashMap<>();
        //Dave's debt: owes (120/4)=30 + owes (68/4=)17 + owes (15/3=)5 + paid 20 = €32.00 debt
        expectedDebts.put("Dave", 32.0f);
        //Eva's debt: paid 120 + owes (120/4=)30 + owes (68/4=)17 + owes (15/3=)5 + owes 20 = €48.00 owed
        expectedDebts.put("Eva", -48.0f);
        //Mark's debt: owes (120/4=)30 + paid 68 + owes (68/4=)17 = €21.00 owed
        expectedDebts.put("Mark", -21.0f);
        //Anne's debts: owes (120/4)=30 + owes (68/4=)17 + paid 15 + owes (15/3=)5 = €37.00 debt
        expectedDebts.put("Anne", 37.0f);
        return expectedDebts;
    }
}
