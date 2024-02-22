import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class ExpenseTest {

    @BeforeEach
    void setupForTests() {
        List<String> participants = Arrays.asList("Ivo", "Philip", "Sinan");
        Expense expense = new Expense("Yannick", "22-02-2024", 40.60f, "Meeting Lunch", participants);
    }

    @Test
    void testGetters() {
        assertEquals("Meeting Lunch", expense.getDescription());
        assertEquals("Yannick", expense.getOwner());
        assertEquals("22-02-2024", expense.getDate());
        assertEquals(40.60f, expense.getAmount());
        assertEquals(3, expense.getParticipants().size());
    }

    @Test
    void testSetters() {
        expense.setDescription("Snacks");
        expense.setOwner("Ivo");
        expense.setDate("16-06-2020");
        expense.setAmount(10.00f);

        assertEquals("Snacks", expense.getDescription());
        assertEquals("Ivo", expense.getOwner());
        assertEquals("16-06-2020", expense.getDate());
        assertEquals(10.00f, expense.getAmount());
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
