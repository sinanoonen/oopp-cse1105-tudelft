import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @BeforeEach
    void setupForTests(){
        Transaction transaction = new TestableTransaction("John Example", "22-02-2024", 40.60f);
    }

    @Test
    void testGetters() {
        assertEquals("John Example", transaction.getOwner());
        assertEquals("22-02-2024", transaction.getDate());
        assertEquals(40.60f, transaction.getAmount());
    }

    @Test
    void testSetters() {
        transaction.setOwner("Johnathan Attempt");
        transaction.setDate("16-06-2020");
        transaction.setAmount(55.50f);
        assertEquals("Johnathan Attempt", transaction.getOwner());
        assertEquals("16-06-2020", transaction.getDate());
        assertEquals(55.50f, transaction.getAmount());
    }

    @Test
    void testToString() {
        String expected = "Transaction{owner = 'John Example', " +
                "date = '22-02-2024', amount = 40.60}";
        assertEquals(expected, transaction.toString());
    }

    @Test
    void testEqualsAndTestHashCode() {
        Transaction identicalTransaction = new TestableTransaction("John Example", "22-02-2024", 40.60f);
        Transaction differentTransaction = new TestableTransaction("Johnathan Attempt", "16-06-2020", 55.50f);
        assertEquals(transaction, identicalTransaction);
        assertEquals(transaction.hashCode(), identicalTransaction.hashCode());
        assertNotEquals(transaction, differentTransaction);
        assertNotEquals(transaction.hashCode(), differentTransaction.hashCode());
    }
}