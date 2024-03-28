package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ExchangeRatesTest {

    private ExchangeRates exchangeRates;
    private final String testBase = "USD";
    private final long testTimestamp = 1618329157L;
    private final String testDisclaimer = "Test Disclaimer";
    private final String testLicense = "Test License";
    private final Map<String, Double> testRates = new HashMap<>();

    @BeforeEach
    public void setup() {
        exchangeRates = new ExchangeRates();
        testRates.put("EUR", 0.85);
        testRates.put("JPY", 110.25);

        exchangeRates.setBase(testBase);
        exchangeRates.setTimestamp(testTimestamp);
        exchangeRates.setDisclaimer(testDisclaimer);
        exchangeRates.setLicense(testLicense);
        exchangeRates.setRates(testRates);
    }

    @Test
    public void whenGettingBaseThenCorrectBaseIsReturned() {
        assertEquals(testBase, exchangeRates.getBase());
    }

    @Test
    public void whenGettingTimestampThenCorrectTimestampIsReturned() {
        assertEquals(testTimestamp, exchangeRates.getTimestamp());
    }

    @Test
    public void whenGettingDisclaimerThenCorrectDisclaimerIsReturned() {
        assertEquals(testDisclaimer, exchangeRates.getDisclaimer());
    }

    @Test
    public void whenGettingLicense_thenCorrectLicenseIsReturned() {
        assertEquals(testLicense, exchangeRates.getLicense());
    }

    @Test
    public void whenGettingRates_thenCorrectRatesAreReturned() {
        assertEquals(testRates, exchangeRates.getRates());
    }

    @Test
    public void testSettersAndGetters() {
        String newBase = "EUR";
        long newTimestamp = 1618329200L;
        String newDisclaimer = "New Disclaimer";
        String newLicense = "New License";
        Map<String, Double> newRates = new HashMap<>();
        newRates.put("USD", 1.18);
        newRates.put("JPY", 130.50);

        exchangeRates.setBase(newBase);
        exchangeRates.setTimestamp(newTimestamp);
        exchangeRates.setDisclaimer(newDisclaimer);
        exchangeRates.setLicense(newLicense);
        exchangeRates.setRates(newRates);

        assertEquals(newBase, exchangeRates.getBase());
        assertEquals(newTimestamp, exchangeRates.getTimestamp());
        assertEquals(newDisclaimer, exchangeRates.getDisclaimer());
        assertEquals(newLicense, exchangeRates.getLicense());
        assertEquals(newRates, exchangeRates.getRates());
    }

    @Test
    public void testEqualsAndHashCode() {
        ExchangeRates anotherExchangeRates = new ExchangeRates();
        anotherExchangeRates.setBase(testBase);
        anotherExchangeRates.setTimestamp(testTimestamp);
        anotherExchangeRates.setDisclaimer(testDisclaimer);
        anotherExchangeRates.setLicense(testLicense);
        anotherExchangeRates.setRates(testRates);

        assertEquals(exchangeRates, anotherExchangeRates);
        assertEquals(exchangeRates.hashCode(), anotherExchangeRates.hashCode());
    }

    @Test
    public void testNotEquals() {
        ExchangeRates differentExchangeRates = new ExchangeRates();
        differentExchangeRates.setBase("GBP");
        differentExchangeRates.setTimestamp(1618329200L);
        differentExchangeRates.setDisclaimer("Different Disclaimer");
        differentExchangeRates.setLicense("Different License");
        Map<String, Double> differentRates = new HashMap<>();
        differentRates.put("USD", 1.3);
        differentRates.put("EUR", 1.1);
        differentExchangeRates.setRates(differentRates);

        assertNotEquals(exchangeRates, differentExchangeRates);
    }
}