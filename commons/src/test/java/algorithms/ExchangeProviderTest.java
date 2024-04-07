package algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * This class is to test the exchange between currencies.
 */
public class ExchangeProviderTest {

    @BeforeEach
    void prefetchRates() {
        ExchangeRates rates = ExchangeProvider.getExchangeRates();
    }

    @Test
    void testGetExchangeRates() {
        ExchangeRates rates = ExchangeProvider.getExchangeRates();
        assertNotNull(rates);
        assertNotEquals(0, rates.getRates().size());
    }

    @Test
    void testGetApiUrl() {
        String url = ExchangeProvider.getApiUrl();
        assertNotNull(url);
    }

    @Test
    void testGetApiKey() {
        String key = ExchangeProvider.getApiKey();
        assertNotNull(key);
    }

    @Test
    void testConvertCurrencyUSD_EUR() {
        double amount = 100;
        double rate = ExchangeProvider.getExchangeRates().getRates().get("EUR")
                / ExchangeProvider.getExchangeRates().getRates().get("USD");
        double actualConverted = amount * rate;
        double converted = ExchangeProvider.convertCurrency(amount, "USD", "EUR");

        assertEquals(Math.round(actualConverted), Math.round(converted));

        double convertedBack = ExchangeProvider.convertCurrency(converted, "EUR", "USD");
        assertEquals(Math.round(amount), Math.round(convertedBack));
    }

    @Test
    void testConvertCurrencyGBP_CNY() {
        double amount = 100;
        double rate = ExchangeProvider.getExchangeRates().getRates().get("CNY")
                / ExchangeProvider.getExchangeRates().getRates().get("GBP");
        double actualConverted = amount * rate;
        double converted = ExchangeProvider.convertCurrency(amount, "GBP", "CNY");

        assertEquals(Math.round(actualConverted), Math.round(converted));

        double convertedBack = ExchangeProvider.convertCurrency(converted, "CNY", "GBP");
        assertEquals(Math.round(amount), Math.round(convertedBack));
    }


}
