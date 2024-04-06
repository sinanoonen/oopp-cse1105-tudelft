package client.utils;

import commons.Currency;

/**
 * Utility class for the client.
 */
public class ClientUtils {

    private static Currency currency;
    private static Language language;
    private static boolean highContrast;

    public static void setCurrency(Currency currency) {
        ClientUtils.currency = currency;
    }

    public static Currency getCurrency() {
        return currency;
    }

    public static void setLanguage(Language language) {
        ClientUtils.language = language;
    }

    public static Language getLanguage() {
        return language;
    }

    public static void setHighContrast(boolean highContrast) {
        ClientUtils.highContrast = highContrast;
    }

    public static boolean isHighContrast() {
        return highContrast;
    }
}
