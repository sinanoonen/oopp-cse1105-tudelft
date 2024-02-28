package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParticipantTest {
    private Participant participant1;
    private Participant participant2;
    private Participant participant3;

    @BeforeEach
    public void setUp() {
        participant1 = new Participant("Test Name", 100.0f);
        participant2 = new Participant("Test2 Name");
        participant3 = new Participant("Test Name", 100.0f);
    }

    @Test
    public void testGetName() {
        assertEquals("Test Name", participant1.getName());
    }

    @Test
    public void testGetDebt() {
        assertEquals(100.0f, participant1.getDebt(), 0.0f);
    }

    @Test
    public void testSetName() {
        participant1.setName("New Name");
        assertEquals("New Name", participant1.getName());
    }

    @Test
    public void testSetDebt() {
        participant1.setDebt(200.0f);
        assertEquals(200.0f, participant1.getDebt(), 0.0f);
    }

    @Test
    public void testEqualsWithSameObject() {
        assertEquals(participant1, participant1);
    }

    @Test
    public void testEqualsWithDifferentObjectSameValues() {
        assertEquals(participant1, participant3);
    }

    @Test
    public void testEqualsWithNull() {
        assertNotEquals(null, participant1);
    }

    @Test
    public void testHashCode() {
        assertEquals(participant1.hashCode(), participant3.hashCode());
        assertNotEquals(participant1.hashCode(), participant2.hashCode());
    }

    @Test
    public void testToString() {
        String expected = "Participant{name='Test Name', debt=100.0}";
        assertEquals(expected, participant1.toString());
    }
}