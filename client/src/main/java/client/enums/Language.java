package client.utils;

/**
 * Enum for the currency.
 */
public enum Language {
    ENGLISH("EN"),
    DUTCH("NL"),
    TURKISH("TR");

    public final String code;

    private Language(String code) {
        this.code = code;
    }
}
