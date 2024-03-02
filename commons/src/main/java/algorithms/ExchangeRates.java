package algorithms;

import java.util.Map;

/**
 * This class is for exchanging between currencies.
 */
public class ExchangeRates {
    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    private Map<String, Double> rates;

    /**
     * Getter for the rates.
     *
     * @return a map with all the rates
     */
    public Map<String, Double> getRates() {
        return rates;
    }

    /**
     * Getter for the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for the base.
     *
     * @return the base
     */
    public String getBase() {
        return base;
    }
}
