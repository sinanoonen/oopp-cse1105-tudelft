package client.utils;

import commons.Currency;
import commons.EmailConfig;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Singleton class to read client config file.
 */
public class ConfigReader {
    private static final String CONFIG_FILE = "config.properties";
    private static final String CONFIG_PATH = "src/main/resources/" + CONFIG_FILE;
    private static final Properties PROPERTIES = new Properties();
    private static final String DEFAULT_IP = "localhost";
    private static final String DEFAULT_PORT = "8080";

    private ConfigReader() {}

    /**
     * Loads configs into ConfigReader object.
     *
     * @throws FileNotFoundException if config file path does not lead to a config file
     * @throws IOException if error occurred while reading config file
     */
    @SuppressWarnings("checkstyle:CommentsIndentation")
    public static void initialize() throws IOException {

// FileInputStream is = new FileInputStream(CONFIG_FILE);
// PROPERTIES.load(is);
// is.close();

        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                PROPERTIES.load(is);
            } else {
                throw new FileNotFoundException("config.properties not found in the classpath");
            }
        }
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
            case "TR" -> Language.TURKISH;
            default -> Language.ENGLISH;
        };
    }

    public static String getIP() {
        String ip = PROPERTIES.getProperty("ip");
        return ip == null ? DEFAULT_IP : ip;
    }

    public static String getPort() {
        String port = PROPERTIES.getProperty("port");
        return port == null ? DEFAULT_PORT : port;
    }

    /**
     * Reads the selected currency from config file.
     *
     * @return Currency in config file, EUR if none found
     */
    public static Currency getCurrency() {
        return switch (PROPERTIES.getProperty("currency")) {
            case "USD" -> Currency.USD;
            case "GBP" -> Currency.GBP;
            case "JPY" -> Currency.JPY;
            case "CNY" -> Currency.CNY;
            default -> Currency.EUR;
        };
    }

    public static String getEmailHost() {
        String host = PROPERTIES.getProperty("email.host");
        return (host != null && !host.isEmpty()) ? host : null;
    }

    /**
     * This is used to get email port from the config file.
     *
     * @return the email port
     */
    public static int getEmailPort() {
        String portStr = PROPERTIES.getProperty("email.port");
        try {
            return (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String getEmailUsername() {
        String username = PROPERTIES.getProperty("email.username");
        return (username != null && !username.isEmpty()) ? username : null;
    }

    public static String getEmailPassword() {
        String password = PROPERTIES.getProperty("email.password");
        return (password != null && !password.isEmpty()) ? password : null;
    }

    public static EmailConfig getEmailConfig() {
        return new EmailConfig(getEmailHost(), getEmailPort(), getEmailUsername(), getEmailPassword());
    }

    /**
     * Writes the language to the config file.
     */
    public static void writeLanguage(Language language) {
        PROPERTIES.setProperty("language", language.code);
        try (OutputStream os = new FileOutputStream(CONFIG_PATH)) {
            PROPERTIES.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the currency to the config file.
     */
    public static void writeCurrency(Currency currency) {
        PROPERTIES.setProperty("currency", currency.toString());
        try (OutputStream os = new FileOutputStream(CONFIG_PATH)) {
            PROPERTIES.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the IP address to the properties file.
     */
    public static void writeIP(String ip) {
        PROPERTIES.setProperty("ip", ip);
        try (OutputStream os = new FileOutputStream(CONFIG_PATH)) {
            PROPERTIES.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the port to the properties file.
     *
     */
    public static void writePort(String port) {
        PROPERTIES.setProperty("port", port);
        try (OutputStream os = new FileOutputStream(CONFIG_PATH)) {
            PROPERTIES.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
