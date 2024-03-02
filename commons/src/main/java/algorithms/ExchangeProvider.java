package algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;

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
    public static Map<String, Double> getExchangeRates() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_URL + "?base=USD&app_id=" + API_KEY))
                .build();

        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return Collections.emptyMap();
        }

        System.out.println(response.statusCode());
        //System.out.println(response.body());

        ObjectMapper mapper = new ObjectMapper();
        try {
            ExchangeRates exchangeRates = mapper.readValue(response.body(), ExchangeRates.class);
            System.out.println(exchangeRates.getBase());
            return exchangeRates.getRates();
        } catch (IOException e) {
            return Collections.emptyMap();
        }

    }

}
