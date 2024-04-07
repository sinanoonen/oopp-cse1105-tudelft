package server;

import commons.EmailConfig;
import java.util.Properties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * This is the service to test the mail.
 */
@Service
public class EmailService {

    /**
     * This methods actually sends an email.
     *
     * @param emailConfig the email config
     * @param recipient the recipient
     * @param subject the subject
     * @param text the text
     */
    public void sendEmail(EmailConfig emailConfig, String recipient, String subject, String text) {
        JavaMailSender mailSender = createMailSender(emailConfig);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailConfig.getUsername());
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private JavaMailSender createMailSender(EmailConfig emailConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfig.getHost());
        mailSender.setPort(emailConfig.getPort());
        mailSender.setUsername(emailConfig.getUsername());
        mailSender.setPassword(emailConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

}