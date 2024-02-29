package commons;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserKey implements Serializable {
    private String email;
    private String iban;

    public UserKey() {

    }

    public UserKey(String email, String iban) {
        this.email = email;
        this.iban = iban;
    }

    public String getEmail() {
        return email;
    }

    public String getIban() {
        return iban;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof UserKey userKey)) return false;
        return Objects.equals(email, userKey.email) &&
            Objects.equals(iban, userKey.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, iban);
    }
}
