package algorithms;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * This class is to test the exchange between currencies.
 */
public class ExchangeTest {

    @Test
    void seeRates() {
        Map<String, Double> rates = ExchangeProvider.getExchangeRates();
        System.out.println(rates);
        System.out.println(rates.get("EUR"));
        assertNotEquals(0, rates.size());
    }
}
