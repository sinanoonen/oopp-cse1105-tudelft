package algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


/**
 * This class is to test the exchange between currencies.
 */
public class ExchangeProviderTest {

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

    @AfterEach
    void testFileCaching() {
        ExchangeRates rates = ExchangeProvider.getExchangeRates();
        File file = new File("src/main/resources/exchangeRates.txt");
        assert file.exists();
        assert file.length() > 0;
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExchangeRates cachedRates = mapper.readValue(file, ExchangeRates.class);
            assertEquals(rates, cachedRates);
        } catch (Exception e) {
            throw new RuntimeException("Error reading cached exchange rates in test", e);
        }
    }

    @AfterEach
    void testFileCachingExpiration() {
        File file = new File("src/main/resources/exchangeRates.txt");
        long lastModified = System.currentTimeMillis() - 300001;
        assert file.setLastModified(lastModified);

        ExchangeProvider.getExchangeRates();
        assert file.exists();
        assert file.length() > 0;
        assertNotEquals(lastModified, file.lastModified());
    }


}
