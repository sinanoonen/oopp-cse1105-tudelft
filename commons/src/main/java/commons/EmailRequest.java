package commons;

import java.util.Objects;

/**
 * This is the EmailRequest with all the data
 * necessary for sending an email.
 */
public class EmailRequest {
    private EmailConfig emailConfig;
    private String recipient;
    private String subject;
    private String message;

    /**
     * Empty constructor.
     */
    public EmailRequest() {
    }

    /**
     * Default constructor.
     *
     * @param emailConfig the email config
     * @param recipient the recipient
     * @param subject the subject
     * @param message the message
     */
    public EmailRequest(EmailConfig emailConfig, String recipient, String subject, String message) {
        this.emailConfig = emailConfig;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }

    /**
     * Getter for the email config.
     *
     * @return the email config
     */
    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    /**
     * Setter for the email config.
     *
     * @param emailConfig sets the email config
     */
    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    /**
     * Getter for the recipient.
     *
     * @return the recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Setter for the recipient.
     *
     * @param recipient the recipient to be set
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Getter for the subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject the subject to be set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Getter for the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for the message.
     *
     * @param message the message to be set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * The equals method.
     *
     * @param object the object to be compared to.
     * @return true or false
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof EmailRequest that)) {
            return false;
        }
        return Objects.equals(emailConfig, that.emailConfig)
            && Objects.equals(recipient, that.recipient)
            && Objects.equals(subject, that.subject)
            && Objects.equals(message, that.message);
    }

    /**
     * Generates a hash code.
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(emailConfig, recipient, subject, message);
    }
}
