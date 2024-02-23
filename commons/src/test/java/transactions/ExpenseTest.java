package transactions;

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
        List<String> participants = Arrays.asList("Ivo", "Philip", "Sinan");
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
        assertEquals(3, expense.getParticipants().size());
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
        String expected = "Expense{Transaction{owner = 'Yannick', "
                + "date = '22-02-2024', amount = 40.60}"
                + "description='Meeting Lunch, participants={Ivo, Philip, Sinan}}";
        assertEquals(expected, expense.toString());
    }

    @Test
    void testEqualsAndTestHashCode() {
        List<String> participantsIdentical = Arrays.asList("Ivo", "Philip", "Sinan");
        List<String> participantsDifferent = Arrays.asList("Yannick", "Philip", "Sinan");
        Expense expenseIdentical = new Expense("Yannick", baseDate, 40.60f, "Meeting Lunch", participantsIdentical);
        Expense expenseParticipantsDifferent = new Expense("Yannick", baseDate, 40.60f, "Meeting Lunch", participantsDifferent);
        Expense expenseOtherDifferent = new Expense("Ivo", baseDate, 40.60f, "Meeting Lunch", participantsIdentical);
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
        assertEquals(10.15f, expense.getParticipants().get("Ivo"));
        assertFalse(expense.modifyParticipant("Emilio", 5.50f));
    }

    @Test
    void testRemoveParticipant() {
        assertTrue(expense.removeParticipant("Ivo"));
        assertFalse(expense.getParticipants().containsKey("Ivo"));
        assertEquals(2, expense.getParticipants().size());
        assertFalse(expense.removeParticipant("Emilio"));
    }
}
