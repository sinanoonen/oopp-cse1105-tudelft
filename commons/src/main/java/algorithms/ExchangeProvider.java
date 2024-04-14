package algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This class provides the exchange rates from the Open Exchange Rates API.
 * The API is free to use and requires an API key to access the data.
 * The API returns the exchange rates in JSON format.
 * The base currency is EUR.
 * The API documentation can be found at <a href="https://openexchangerates.org/documentation">...</a>
 */
public class ExchangeProvider {

    private static final String API_URL = "https://openexchangerates.org/api/latest.json";
    private static final String API_KEY = "4503cd84a4754c90b993b4952cbd3b37";

    public static String getApiUrl() {
        return API_URL;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    /**
     * Get the exchange rates from the API.
     *
     * @return A map of currency codes and exchange rates
     */
    public static ExchangeRates getExchangeRates() {
        // if exchange rates are already cached and not older than 5 minutes, return them
        File file = new File("src/main/resources/exchangeRates.txt");
        if (file.exists()
                && System.currentTimeMillis() - file.lastModified() < 300000) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(file, ExchangeRates.class);
            } catch (IOException e) {
                System.out.println("Error reading cached exchange rates, caching again...");
            }
        }


        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_URL + "?base=USD&app_id=" + API_KEY))
                .build();

        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error sending request", e);
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            ExchangeRates exchangeRates = mapper
                    .readValue(response.body(), ExchangeRates.class);

            // caches exchange rates to a file (create file if it doesn't exist)
            try {
                mapper.writeValue(
                        file,
                        exchangeRates
                );
            } catch (IOException e) {
                System.out.println("Error writing exchange rates to file: " + e);
            }

            return exchangeRates;
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }

    }

    /**
     * Convert an amount from one currency to another.
     *
     * @param amount The amount to convert
     * @param fromCurrency The currency to convert from
     * @param toCurrency The currency to convert to
     * @return The converted amount
     */
    public static double convertCurrency(double amount, String fromCurrency, String toCurrency) {
        ExchangeRates exchangeRates = getExchangeRates();
        double rate = exchangeRates.getRates().get(toCurrency) / exchangeRates.getRates().get(fromCurrency);
        return amount * rate;
    }

}
