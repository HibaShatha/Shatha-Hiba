package Service;

import backend.model.User;
import backend.service.SMSNotifier;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class SMSNotifierTest {

    @Test
    public void testNotifyWritesToFile() throws IOException {
        // نستخدم ملف مؤقت بدل sms.csv الأصلي
        String tempFile = "test_sms.csv";

        // إنشاء المستخدم
        User user = new User("john", "pass", "john@test.com", "1234567890");
        String message = "You have overdue books!";

        // استدعاء notify
        SMSNotifier notifier = new SMSNotifier();
        notifier.notify(user, message);

        // نقرأ الملف sms.csv (في هالتست استخدمي sms.csv أو غيري اسم الملف في notifier نفسه)
        List<String> lines = Files.readAllLines(Path.of("sms.csv"));
        boolean found = lines.stream().anyMatch(line -> line.contains("1234567890") && line.contains("overdue books"));
        assertTrue(found, "Message should be written to the SMS log file");

         Files.deleteIfExists(Path.of("sms.csv"));
    }
}