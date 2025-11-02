package backend.service;

import java.io.*;
import java.util.*;

/**
 * Service class for reading mock email notifications from a log file.
 * <p>
 * Emails are stored in a text file (emails.txt by default) in the format:
 * "to: {email} | message: {message content}"
 * This class allows retrieving messages sent to a specific email address.
 * </p>
 * 
 * @author Shatha_Dweikat
 * @version 1.0.0
 */
public class EmailService {
    /** Path to the file containing mock emails */
    private final String emailFile;

    /**
     * Default constructor using "emails.txt" as the log file.
     */
    public EmailService() {
        this("emails.txt");
    }

    /**
     * Constructor allowing custom log file path.
     *
     * @param emailFile path to the email log file
     */
    public EmailService(String emailFile) {
        this.emailFile = emailFile;
    }

    /**
     * Retrieves all messages sent to the specified email address.
     *
     * @param email the email address to search for
     * @return a list of message strings sent to this email
     */
    public List<String> getMessagesForEmail(String email) {
        List<String> messages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(emailFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("to: " + email + " |")) {
                    String msg = line.substring(line.indexOf("| message: ") + 11);
                    messages.add(msg);
                }
            }
        } catch (IOException e) {
            // Errors ignored for simplicity; could log internally if needed
        }
        return messages;
    }
}
