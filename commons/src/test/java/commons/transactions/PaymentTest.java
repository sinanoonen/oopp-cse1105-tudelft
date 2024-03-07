package commons.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test for the Payment class.
 */
public class PaymentTest {
    Payment paymentSenderOwner;
    Payment paymentSenderNotOwner;
    LocalDate paymentDate;

    @BeforeEach
    void setUpForTests() {
        paymentDate = LocalDate.of(2024, 2, 23);
        paymentSenderNotOwner = new Payment("Emilio", paymentDate, 20f, "Yannick", "Filip");
        paymentSenderOwner = new Payment("Emilio", paymentDate, 30f, "Yannick");
    }

    @Test
    void testGetters() {
        assertEquals("Filip", paymentSenderNotOwner.getSender());
        assertEquals("Yannick", paymentSenderNotOwner.getRecipient());
        assertEquals(paymentDate, paymentSenderNotOwner.getDate());
        assertEquals("Emilio", paymentSenderNotOwner.getOwner());
        assertEquals(30f, paymentSenderOwner.getAmount());
        assertEquals("Emilio", paymentSenderOwner.getOwner());
        assertEquals("Emilio", paymentSenderOwner.getSender());
        assertEquals(paymentSenderOwner.getOwner(), paymentSenderOwner.getSender());

        //TODO Make it so the Id is not always 0
        //assertNotEquals(paymentSenderNotOwner.getId(), paymentSenderOwner.getId());
    }

    @Test
    void testSetters() {
        LocalDate otherDate = LocalDate.of(2020, Calendar.JUNE, 16);
        paymentSenderNotOwner.setSender("Ivo");
        paymentSenderNotOwner.setRecipient("Sinan");
        paymentSenderNotOwner.setDate(otherDate);
        paymentSenderNotOwner.setOwner("Yannick");
        paymentSenderNotOwner.setAmount(15f);

        paymentSenderOwner.setSender("Ivo");
        paymentSenderOwner.setOwner("Ivo");

        assertEquals("Ivo", paymentSenderNotOwner.getSender());
        assertEquals("Sinan", paymentSenderNotOwner.getRecipient());
        assertEquals(otherDate, paymentSenderNotOwner.getDate());
        assertEquals("Yannick", paymentSenderNotOwner.getOwner());
        assertEquals(15f, paymentSenderNotOwner.getAmount());
        assertEquals(paymentSenderOwner.getSender(), "Ivo");
        assertEquals(paymentSenderOwner.getOwner(), "Ivo");
    }

    @Test
    void testToString() {
        String expected = "Payment{Transaction{owner = 'Emilio', "
                + "date = '2024-02-23', amount = 20.0}sender='Filip',"
                + " recipient='Yannick'}";
        assertEquals(expected, paymentSenderNotOwner.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        assertEquals(paymentSenderNotOwner, paymentSenderNotOwner);
        assertNotEquals(paymentSenderNotOwner, null);

        Payment paymentIdentical = new Payment("Emilio", paymentDate,
                20f, "Yannick", "Filip");
        assertEquals(paymentSenderNotOwner, paymentIdentical);

        Payment paymentDifferent = new Payment("Sinan", paymentDate,
                30f, "Yannick", "Filip");
        assertNotEquals(paymentSenderNotOwner, paymentDifferent);

        Payment paymentIdentical2 = new Payment("Emilio", paymentDate,
                30f, "Yannick");
        assertEquals(paymentSenderOwner, paymentIdentical2);

        Payment paymentDifferent2 =  new Payment("Ivo", paymentDate,
                40f, "Yannick");
        assertNotEquals(paymentSenderOwner, paymentDifferent2);

        Expense expense = new Expense("Emilio", paymentDate, 20f,
                "Yannick", List.of("Filip"));
        assertNotEquals(paymentSenderNotOwner, expense);

        assertNotEquals(paymentSenderNotOwner.hashCode(), paymentSenderOwner.hashCode());
    }

}