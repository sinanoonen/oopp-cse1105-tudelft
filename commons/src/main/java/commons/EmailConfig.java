package commons;

import java.util.Objects;

public class EmailConfig {
    private String host;
    private int port;
    private String username;
    private String password;

    public EmailConfig() {
    }

    public EmailConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof EmailConfig that)) return false;
        return port == that.port && Objects.equals(host, that.host) &&
            Objects.equals(username, that.username) &&
            Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, username, password);
    }

    public boolean isComplete() {
        boolean isHostValid = this.host != null;
        boolean isPortValid = this.port > 0;
        boolean isUsernameValid = this.username != null;
        boolean isPasswordValid = this.password != null;

        return isHostValid && isPortValid && isUsernameValid && isPasswordValid;
    }

}
