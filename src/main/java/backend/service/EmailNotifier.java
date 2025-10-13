package backend.service;

import backend.model.User;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EmailNotifier implements Observer {
    private final String LOG_FILE = "emails.txt";

    @Override
    public void notify(User user, String message) {
        String email = user.getEmail();
        String formattedMessage = "Hello " + user.getUsername() + ", " + message;
        System.out.println("[Mock Email to " + email + "] " + formattedMessage);
        logMockEmail(email, formattedMessage);
    }

    private void logMockEmail(String email, String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write("to: " + email + " | message: " + message);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}