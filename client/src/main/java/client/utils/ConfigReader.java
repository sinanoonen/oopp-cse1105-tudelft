package client.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton class to read client config file.
 */
public class ConfigReader {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = new Properties();

    private ConfigReader() {}

    /**
     * Loads configs into ConfigReader object.
     *
     * @throws FileNotFoundException if config file path does not lead to a config file
     * @throws IOException if error occurred while reading config file
     */
    public static void initialize() throws IOException {
        FileInputStream is = new FileInputStream(CONFIG_FILE);
        PROPERTIES.load(is);
        is.close();
    }

    /**
     * Reads the language from the config file.
     *
     * @return language stored in config file, english if none found
     */
    public static Language getLanguage() {
        return switch (PROPERTIES.getProperty("language")) {
            case "EN" -> Language.ENGLISH;
            case "NL" -> Language.DUTCH;
            case "IT" -> Language.ITALIAN;
            default -> Language.ENGLISH;
        };
    }

    public static String getIP() {
        return PROPERTIES.getProperty("ip");
    }

    public static String getPort() {
        return PROPERTIES.getProperty("port");
    }

    /**
     * Reads the selected currency from config file.
     *
     * @return Currency in config file, EUR if none found
     */
    public static Currency getCurrency() {
        return switch (PROPERTIES.getProperty("currency")) {
            case "EUR" -> Currency.EUR;
            case "USD" -> Currency.USD;
            case "GBP" -> Currency.GBP;
            case "JPY" -> Currency.JPY;
            case "CNY" -> Currency.CNY;
            default -> Currency.EUR;
        };
    }
}
