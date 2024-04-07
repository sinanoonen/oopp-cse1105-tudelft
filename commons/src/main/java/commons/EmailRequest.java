package commons;

import java.util.Objects;

public class EmailRequest {
    private EmailConfig emailConfig;
    private String recipient;
    private String subject;
    private String message;

    public EmailRequest() {
    }

    public EmailRequest(EmailConfig emailConfig, String recipient, String subject, String message) {
        this.emailConfig = emailConfig;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }

    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof EmailRequest that)) return false;
        return Objects.equals(emailConfig, that.emailConfig) &&
            Objects.equals(recipient, that.recipient) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailConfig, recipient, subject, message);
    }
}
