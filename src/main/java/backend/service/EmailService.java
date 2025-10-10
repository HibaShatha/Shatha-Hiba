package backend.service;

import java.io.*;
import java.util.*;

public class EmailService {
    private final String EMAIL_FILE = "emails.txt";

    public List<String> getMessagesForEmail(String email) {
        List<String> messages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EMAIL_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("to: " + email + " |")) {
                    String msg = line.substring(line.indexOf("| message: ") + 11);
                    messages.add(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
