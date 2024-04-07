package commons;

import java.util.Objects;

/**
 * A classed with the email config data.
 */
public class EmailConfig {
    private String host;
    private int port;
    private String username;
    private String password;

    /**
     * Empty constructor.
     */
    public EmailConfig() {
    }

    /**
     * Constructor for Email Config.
     *
     * @param host the host
     * @param port the port
     * @param username the username
     * @param password the password
     */
    public EmailConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for the host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Setter for the host.
     *
     * @param host the host to be set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Getter for the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter for the port.
     *
     * @param port the port to be set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Getter for the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the username.
     *
     * @param username the username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the password.
     *
     * @param password the password to be set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The equals method.
     *
     * @param object the object to compare to
     * @return true or false
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof EmailConfig that)) {
            return false;
        }
        return port == that.port
            && Objects.equals(host, that.host)
            && Objects.equals(username, that.username)
            && Objects.equals(password, that.password);
    }

    /**
     * Generates a hashcode for the object.
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(host, port, username, password);
    }

    /**
     * This checks if the email config is complete.
     *
     * @return true or false
     */
    public boolean isComplete() {
        boolean isHostValid = this.host != null;
        boolean isPortValid = this.port > 0;
        boolean isUsernameValid = this.username != null;
        boolean isPasswordValid = this.password != null;

        return isHostValid && isPortValid && isUsernameValid && isPasswordValid;
    }

}
