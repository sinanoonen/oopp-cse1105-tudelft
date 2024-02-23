import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

public class PaymentTest {
    Payment paymentSenderOwner;
    Payment paymentSenderNotOwner;
    LocalDate paymentDate;

    @BeforeEach
    void setUpforTests(){
        paymentDate = LocalDate.of(2024, 2, 23);
        paymentSenderNotOwner = new Payment("Emilio",paymentDate,20f,"Yannick","Filip");
        paymentSenderOwner = new Payment("Emilio",paymentDate,30f,"Yannick");
    }

    @Test
    void testGetters(){
        assertEquals("Filip",paymentSenderNotOwner.getSender());
        assertEquals("Yannick",paymentSenderNotOwner.getReceiver());
        assertEquals(paymentDate, paymentSenderNotOwner.getDate());
        assertEquals("Emilio",paymentSenderNotOwner.getOwner());
        asertEquals(20f,paymentSenderOwner.getAmount());
        assertEquals("Emilio",paymentSenderOwner.getOwner());
        assertEquals("Emilio",paymentSenderOwner.getSender());
        assertEquals(paymentSenderOwner.getOwner(), paymentSenderOwner.getSender());
    }

    @Test
    void testSetters(){
        Date otherDate = new Date(2020, Calendar.JUNE, 16);
        paymentSenderNotOwner.setSender("Ivo");
        paymentSenderNotOwner.setReceiver("Sinan");
        paymentSenderNotOwner.setDate(otherDate);
        paymentSenderNotOwner.setOwner("Yannick");
        paymentSenderNotOwner.setAmount(15f);

        paymentSenderOwner.setSender("Ivo");
        paymentSenderOwner.setOwner("Ivo");

        assertEquals("Ivo",paymentSenderNotOwner.getSender());
        assertEquals("Sinan",paymentSenderNotOwner.getReceiver());
        assertEquals(otherDate,paymentSenderNotOwner.getDate());
        assertEquals("Yannick",paymentSenderNotOwner.getOwner());
        assertEquals(15f,paymentSenderNotOwner.getAmount());
        assertEquals(paymentSenderOwner.getSender("Ivo"));
        assertEquals(paymentSenderOwner.getOwner("Ivo"));
    }

    @Test
    void testToString(){
        String expected = "Payment{Transaction{owner = 'Emilio', " +
                "date = '23-02-2024', amount = 20}" + "sender=Filip" + '\'' +  "recipient=Yannick" + '\'' + "}";
        assertEquals(expected, paymentSenderNotOwner.toString());
    }

    @Test
    void testEqualsAndHashCode(){
        Payment paymentIdentical = new Payment("Emilio",paymentDate,20f,"Yannick","Filip");
        Payment paymentDifferent = new Payment("Sinan",paymentDate,30f,"Yannick","Filip");
        Payment paymentIdentical2 = new Payment("Emilio",paymentDate,30f,"Yannick");
        Payment paymentDifferent2 =  new Payment("Ivo",paymentDate,40f,"Yannick");

        assertEquals(paymentIdentical,paymentSenderNotOwner);
        assertEquals(paymentIdentical2,paymentSenderOwner);
        assertNotEquals(paymentDifferent,paymentSenderNotOwner);
        assertNotEquals(paymentDifferent2,paymentSenderOwner);
    }

}