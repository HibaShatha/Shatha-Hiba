// src/backend/service/RealEmailNotifier.java

package backend.service;

import backend.model.User;
import io.github.cdimascio.dotenv.Dotenv;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class RealEmailNotifier implements Observer {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String FROM_EMAIL = dotenv.get("EMAIL_USERNAME");
    private static final String PASSWORD = dotenv.get("EMAIL_PASSWORD");

    private final Session session;

    public RealEmailNotifier() {
        if (PASSWORD == null || PASSWORD.trim().isEmpty()) {
            System.err.println("Warning: Email password is missing in .env file!");
            System.err.println("Please add EMAIL_PASSWORD to the .env file.");
            this.session = null;
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
    }

    @Override
    public void notify(User user, String message) {
        if (session == null) {
            System.out.println("[Email Disabled] Password is missing in .env");
            return;
        }

        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.out.println("[Email Skipped] No email found for user: " + user.getUsername());
            return;
        }

        String to = user.getEmail().trim();
        String subject = "Library Reminder: Overdue Books";
        String body = """
            Hello %s,

            %s

            Please return the books as soon as possible to avoid any fines.
            Thank you!

            Library Automated System
            """.formatted(user.getUsername(), message);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM_EMAIL, "Library System"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);

            Transport.send(msg);
            System.out.println("[Email Sent Successfully] To: " + to);

        } catch (Exception e) {
            System.out.println("[Email Sending Failed] To: " + to);
            e.printStackTrace();
        }
    }
}
