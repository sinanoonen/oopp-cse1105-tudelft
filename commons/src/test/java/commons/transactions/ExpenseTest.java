package commons.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test for the Expense class.
 */
class ExpenseTest {
    Expense expense;
    LocalDate baseDate;

    @BeforeEach
    void setupForTests() {
        List<String> participants = Arrays.asList("Ivo", "Filip", "Sinan");
        baseDate = LocalDate.of(2015, 3, 2);
        expense = new Expense("Yannick", baseDate, 90f,
                "Meeting Lunch", participants);
    }

    @Test
    void testGetters() {
        assertEquals("Meeting Lunch", expense.getDescription());
        assertEquals("Yannick", expense.getOwner());
        assertEquals(baseDate, expense.getDate());
        assertEquals(90f, expense.getAmount());

        // When creating a new expense using the default
        // constructor default behaviour should split the debt equally
        Map<String, Float> debts = new HashMap<>();
        debts.put("Ivo", 30f);
        debts.put("Filip", 30f);
        debts.put("Sinan", 30f);
        assertEquals(debts, expense.getDebts());
    }

    @Test
    void testSetters() {
        LocalDate otherDate = LocalDate.of(2020, 1, 16);
        expense.setDescription("Snacks");
        expense.setOwner("Ivo");
        expense.setDate(otherDate);
        expense.setAmount(30.00f);

        Map<String, Float> debts = new HashMap<>();
        debts.put("Ivo", 10f);
        debts.put("Filip", 10f);
        debts.put("Sinan", 10f);

        assertEquals("Snacks", expense.getDescription());
        assertEquals("Ivo", expense.getOwner());
        assertEquals(otherDate, expense.getDate());
        assertEquals(30.00f, expense.getAmount());

        // When updating the amount, the debts should be recalculated (no inconsistencies should be allowed)
        assertEquals(debts, expense.getDebts());

    }

    @Test
    void testToString() {
        String expected = "Expense{Transaction{owner = 'Yannick', "
                + "date = '2015-03-02', amount = 90.0}description='Meeting Lunch', "
                + "debts={Ivo=30.0, Sinan=30.0, Filip=30.0}}";
        assertEquals(expected, expense.toString());
    }

    @Test
    void testEqualsAndTestHashCode() {
        List<String> participantsIdentical = Arrays.asList("Ivo", "Filip", "Sinan");
        List<String> participantsDifferent = Arrays.asList("Yannick", "Filip", "Sinan");

        assertEquals(expense, expense);
        assertNotEquals(expense, null);

        Expense expenseIdentical = new Expense("Yannick", baseDate,
                90f, "Meeting Lunch",
                participantsIdentical);
        Expense expenseParticipantsDifferent = new Expense("Yannick", baseDate,
                90f, "Meeting Lunch",
                participantsDifferent);
        Expense expenseOwnerDifferent = new Expense("Ivo", baseDate, 90f,
                "Meeting Lunch",
                participantsIdentical);
        LocalDate otherDate = LocalDate.of(2020, 1, 16);
        Expense expenseDateDifferent = new Expense("Yannick", otherDate, 90f,
                "Meeting Lunch",
                participantsIdentical);
        Expense expenseAmountDifferent = new Expense("Yannick", baseDate, 30f,
                "Meeting Lunch",
                participantsIdentical);
        Expense expenseDescriptionDifferent = new Expense("Yannick", baseDate, 90f,
                "Snacks",
                participantsIdentical);

        assertNotEquals(expense, expenseDescriptionDifferent);
        assertNotEquals(expense, expenseParticipantsDifferent);
        assertNotEquals(expense, expenseOwnerDifferent);
        assertNotEquals(expense, expenseDateDifferent);
        assertNotEquals(expense, expenseAmountDifferent);
        assertEquals(expense, expenseIdentical);

        assertNotEquals(expense.hashCode(), expenseDescriptionDifferent.hashCode());
        assertNotEquals(expense.hashCode(), expenseParticipantsDifferent.hashCode());
        assertNotEquals(expense.hashCode(), expenseOwnerDifferent.hashCode());
        assertNotEquals(expense.hashCode(), expenseDateDifferent.hashCode());
        assertNotEquals(expense.hashCode(), expenseAmountDifferent.hashCode());
        assertEquals(expense.hashCode(), expenseIdentical.hashCode());
    }

    @Test
    void testSplitEqually() {
        expense.splitEqually(30f);
        Map<String, Float> debts = new HashMap<>();
        debts.put("Ivo", 10f);
        debts.put("Filip", 10f);
        debts.put("Sinan", 10f);

        assertEquals(debts, expense.getDebts());
        assertEquals(90f, expense.getAmount());
    }

    @Test
    void testSplitAmong() {
        Map<String, Integer> userMultiplierMap = new HashMap<>();
        userMultiplierMap.put("Ivo", 2);
        userMultiplierMap.put("Filip", 1);
        userMultiplierMap.put("Sinan", 1);

        expense.splitAmong(40f, userMultiplierMap);
        Map<String, Float> debts = new HashMap<>();
        debts.put("Ivo", 20f);
        debts.put("Filip", 10f);
        debts.put("Sinan", 10f);

        assertEquals(debts, expense.getDebts());
        assertEquals(90f, expense.getAmount());
    }

    
}
