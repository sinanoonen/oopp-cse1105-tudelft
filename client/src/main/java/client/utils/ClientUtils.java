package client.utils;

import client.enums.Language;
import commons.Currency;
import javax.inject.Inject;
import org.springframework.stereotype.Service;


/**
 * Utility class for the client.
 */
@Service
public class ClientUtils {

    private Currency currency;
    private Language language;
    private boolean highContrast;
    private UIUtils uiUtils;

    @Inject
    public ClientUtils(UIUtils uiUtils) {
        this.uiUtils = uiUtils;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    /**
     * Changes the language of the application.
     *
     * @param language language to change to
     */
    public void setLanguage(Language language) {
        if (language.equals(this.language)) {
            return; // Avoid re-reading and reloading language map
        }
        this.language = language;
        uiUtils.loadLanguageMap(language);
    }

    public Language getLanguage() {
        return language;
    }

    public void setHighContrast(boolean highContrast) {
        this.highContrast = highContrast;
    }

    public boolean isHighContrast() {
        return highContrast;
    }
}
