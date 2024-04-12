package client.utils;

import client.enums.Language;
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

    /**
     * Changes the language of the application.
     *
     * @param language language to change to
     */
    public static void setLanguage(Language language) {
        if (language.equals(ClientUtils.language)) {
            return; // Avoid re-reading and reloading language map
        }
        ClientUtils.language = language;
        UIUtils.loadLanguageMap(language);
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
