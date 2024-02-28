package commons.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
        expense = new Expense("Yannick", baseDate, 40.60f,
                "Meeting Lunch", participants);
    }

    @Test
    void testGetters() {
        assertEquals("Meeting Lunch", expense.getDescription());
        assertEquals("Yannick", expense.getOwner());
        assertEquals(baseDate, expense.getDate());
        assertEquals(40.60f, expense.getAmount());
        assertEquals(3, expense.getDebts().size());
    }

    @Test
    void testSetters() {
        LocalDate otherDate = LocalDate.of(2020, 1, 16);
        expense.setDescription("Snacks");
        expense.setOwner("Ivo");
        expense.setDate(otherDate);
        expense.setAmount(10.00f);

        assertEquals("Snacks", expense.getDescription());
        assertEquals("Ivo", expense.getOwner());
        assertEquals(otherDate, expense.getDate());
        assertEquals(10.00f, expense.getAmount());
    }

    @Test
    void testToString() {
        String expected = "Expense{Transaction{owner = 'Yannick', date = '2015-03-02', "
                + "amount = 40.6}description='Meeting Lunch', "
                + "debts={Ivo=0.0, Sinan=0.0, Filip=0.0}}";
        assertEquals(expected, expense.toString());
    }

    @Test
    void testEqualsAndTestHashCode() {
        List<String> participantsIdentical = Arrays.asList("Ivo", "Filip", "Sinan");
        List<String> participantsDifferent = Arrays.asList("Yannick", "Filip", "Sinan");
        Expense expenseIdentical = new Expense("Yannick", baseDate,
                40.60f, "Meeting Lunch",
                participantsIdentical);
        Expense expenseParticipantsDifferent = new Expense("Yannick", baseDate,
                40.60f, "Meeting Lunch",
                participantsDifferent);
        Expense expenseOtherDifferent = new Expense("Ivo", baseDate, 40.60f,
                "Meeting Lunch",
                participantsIdentical);
        assertNotEquals(expense, expenseParticipantsDifferent);
        assertNotEquals(expense, expenseOtherDifferent);
        assertEquals(expense, expenseIdentical);
        assertEquals(expense.hashCode(), expenseIdentical.hashCode());
        assertNotEquals(expense, expenseOtherDifferent);
        assertNotEquals(expense, expenseParticipantsDifferent);
    }

    @Test
    void testModifyParticipant() {
        assertTrue(expense.modifyParticipant("Ivo", 10.15f));
        assertEquals(10.15f, expense.getDebts().get("Ivo"));
        assertFalse(expense.modifyParticipant("Emilio", 5.50f));
    }
}
