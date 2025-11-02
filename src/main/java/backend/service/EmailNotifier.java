package backend.service;

import backend.model.User;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Observer implementation that simulates sending email notifications to users.
 * <p>
 * Instead of sending real emails, it logs the messages to a text file (LOG_FILE).
 * This allows testing without sending actual emails.
 * </p>
 * 
 * @author Shatha_Dweikat
 * @version 1.0.0
 */
public class EmailNotifier implements Observer {
    /** Path to the log file where mock emails are stored */
    public String LOG_FILE = "emails.txt";

    /** Stores the last error message if logging fails */
    private String lastErrorMessage = null;

    /**
     * Sends a mock notification to the user.
     * <p>
     * The message is formatted and logged to the LOG_FILE.
     * </p>
     * 
     * @param user the user to notify
     * @param message the message content
     */
    @Override
    public void notify(User user, String message) {
        String email = user.getEmail();
        String formattedMessage = "Hello " + user.getUsername() + ", " + message;
        try {
            logMockEmail(email, formattedMessage);
        } catch (IOException e) {
            lastErrorMessage = e.getMessage();
        }
    }

    /**
     * Logs the mock email to the log file.
     *
     * @param email the user's email address
     * @param message the formatted message content
     * @throws IOException if writing to the file fails
     */
    protected void logMockEmail(String email, String message) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write("to: " + email + " | message: " + message);
            bw.newLine();
        }
    }

    /**
     * Returns the last error message if logging failed.
     * Useful for testing purposes.
     *
     * @return the last error message or null if no error occurred
     */
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}
