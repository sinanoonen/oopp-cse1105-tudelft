package client.utils;

import client.enums.Language;
import commons.Currency;
import commons.EmailConfig;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.springframework.stereotype.Service;

/**
 * Singleton class to read client config file.
 */
@Service
public class ConfigReader {
    private final String configFile = "config.properties";
    private final String configPath = "src/main/resources/" + configFile;
    private Properties properties = new Properties();
    private String defaultIp = "localhost";
    private String defaultPort = "8080";

    public ConfigReader() {}

    /**
     * Loads configs into ConfigReader object.
     *
     * @throws FileNotFoundException if config file path does not lead to a config file
     * @throws IOException if error occurred while reading config file
     */
    @SuppressWarnings("checkstyle:CommentsIndentation")
    public void initialize() throws IOException {

    // FileInputStream is = new FileInputStream(CONFIG_FILE);
    // PROPERTIES.load(is);
    // is.close();

        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream(configFile)) {
            if (is != null) {
                properties.load(is);
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
    public Language getLanguage() {
        return switch (properties.getProperty("language")) {
            case "EN" -> Language.ENGLISH;
            case "NL" -> Language.DUTCH;
            case "TR" -> Language.TURKISH;
            default -> Language.ENGLISH;
        };
    }

    public String getIP() {
        String ip = properties.getProperty("ip");
        return ip == null ? defaultIp : ip;
    }

    public String getPort() {
        String port = properties.getProperty("port");
        return port == null ? defaultPort : port;
    }

    /**
     * Reads the selected currency from config file.
     *
     * @return Currency in config file, EUR if none found
     */
    public Currency getCurrency() {
        return switch (properties.getProperty("currency")) {
            case "USD" -> Currency.USD;
            case "GBP" -> Currency.GBP;
            case "JPY" -> Currency.JPY;
            case "CNY" -> Currency.CNY;
            default -> Currency.EUR;
        };
    }

    public String getEmailHost() {
        String host = properties.getProperty("email.host");
        return (host != null && !host.isEmpty()) ? host : null;
    }

    /**
     * This is used to get email port from the config file.
     *
     * @return the email port
     */
    public int getEmailPort() {
        String portStr = properties.getProperty("email.port");
        try {
            return (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getEmailUsername() {
        String username = properties.getProperty("email.username");
        return (username != null && !username.isEmpty()) ? username : null;
    }

    public String getEmailPassword() {
        String password = properties.getProperty("email.password");
        return (password != null && !password.isEmpty()) ? password : null;
    }

    public EmailConfig getEmailConfig() {
        return new EmailConfig(getEmailHost(), getEmailPort(), getEmailUsername(), getEmailPassword());
    }

    /**
     * Writes the language to the config file.
     */
    public void writeLanguage(Language language) {
        properties.setProperty("language", language.code);
        try (OutputStream os = new FileOutputStream(configPath)) {
            properties.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the currency to the config file.
     */
    public void writeCurrency(Currency currency) {
        properties.setProperty("currency", currency.toString());
        try (OutputStream os = new FileOutputStream(configPath)) {
            properties.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the IP address to the properties file.
     */
    public void writeIP(String ip) {
        properties.setProperty("ip", ip);
        try (OutputStream os = new FileOutputStream(configPath)) {
            properties.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the port to the properties file.
     *
     */
    public void writePort(String port) {
        properties.setProperty("port", port);
        try (OutputStream os = new FileOutputStream(configPath)) {
            properties.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
