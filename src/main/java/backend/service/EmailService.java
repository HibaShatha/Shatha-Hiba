package backend.service;

import java.io.*;
import java.util.*;

public class EmailService {
    private final String emailFile;

    public EmailService() {
        this("emails.txt"); // القيمة الافتراضية
    }

    public EmailService(String emailFile) {
        this.emailFile = emailFile;
    }

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
            // فقط تجاهل أو سجل داخلي بدون printStackTrace
            // e.printStackTrace();
        }
        return messages;
    }

}
