package backend.service;

import backend.model.User;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EmailNotifier implements Observer {
    protected String LOG_FILE = "emails.txt"; // made non-final & protected for test flexibility
    private String lastErrorMessage = null;

    @Override
    public void notify(User user, String message) {
        String email = user.getEmail();
        String formattedMessage = "Hello " + user.getUsername() + ", " + message;
        System.out.println("[Mock Email to " + email + "] " + formattedMessage);
        try {
            logMockEmail(email, formattedMessage);
        } catch (IOException e) {
            // store the exception message so tests can assert it
            lastErrorMessage = e.getMessage();
            e.printStackTrace();
        }
    }

    protected void logMockEmail(String email, String message) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write("to: " + email + " | message: " + message);
            bw.newLine();
        }
    }

    // Getter so tests can read the stored exception message
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}
