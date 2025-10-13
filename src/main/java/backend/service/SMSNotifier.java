package backend.service;

import backend.model.User;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SMSNotifier implements Observer {
    private final String LOG_FILE = "sms.csv";

    @Override
    public void notify(User user, String message) {
        String phoneNumber = user.getPhoneNumber();
        String formattedMessage = "Hello " + user.getUsername() + ", " + message;
        System.out.println("[Mock SMS to " + phoneNumber + "] " + formattedMessage);
        logMockSMS(phoneNumber, formattedMessage);
    }

    private void logMockSMS(String phoneNumber, String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write("to: " + phoneNumber + " | message: " + message);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}