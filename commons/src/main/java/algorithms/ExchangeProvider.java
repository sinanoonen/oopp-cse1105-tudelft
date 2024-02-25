package algorithms;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

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
    public static Map<String, Double> getExchangeRates() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_URL + "?app_id=" + API_KEY + "&base=EUR"))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());

        //TODO PARSE THE JSON RESPONSE AND RETURN THE RATES

        return null;

    }

}
