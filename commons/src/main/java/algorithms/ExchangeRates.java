package algorithms;

import java.util.Map;
import java.util.Objects;

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
     * Getter for the base.
     *
     * @return the base
     */
    public String getBase() {
        return base;
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
     * Getter for the disclaimer.
     *
     * @return the disclaimer
     */
    public String getDisclaimer() {
        return disclaimer;
    }

    /**
     * Getter for the license.
     *
     * @return the license
     */
    public String getLicense() {
        return license;
    }

    /**
     * Sets the disclaimer.
     *
     * @param disclaimer the disclaimer
     */
    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    /**
     * Sets the license.
     *
     * @param license the license
     */
    public void setLicense(String license) {
        this.license = license;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the base.
     *
     * @param base the base
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * Set the rates.
     *
     * @param rates the rates
     */
    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    /**
     * Equals method of the event.
     *
     * @param object the object to compare to
     * @return true or false
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ExchangeRates that)) {
            return false;
        }
        return timestamp == that.timestamp && Objects.equals(disclaimer, that.disclaimer)
            && Objects.equals(license, that.license)
            && Objects.equals(base, that.base)
            && Objects.equals(rates, that.rates);
    }

    /**
     * Hashcode for the event.
     *
     * @return integer hash of the event object
     */
    @Override
    public int hashCode() {
        return Objects.hash(disclaimer, license, timestamp, base, rates);
    }
}
