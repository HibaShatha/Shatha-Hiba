package Service;

import backend.model.User;
import backend.service.EmailNotifier;
import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmailNotifierTest {

    private static final String LOG_FILE = "emails.txt";
    private EmailNotifier notifier;
    private User user;

    @BeforeEach
    void setUp() {
        notifier = new EmailNotifier();
        user = new User("john_doe", "pass123", "john@example.com", "1234567890");

        // Clean up log file before each test
        File file = new File(LOG_FILE);
        if (file.exists()) file.delete();
    }

    @Test
    @DisplayName("Test notify() writes correct log entry")
    void testNotifyWritesToFile() throws IOException {
        String message = "Your account has been updated.";
        notifier.notify(user, message);

        // Verify the file was created
        File file = new File(LOG_FILE);
        assertTrue(file.exists(), "emails.txt should be created");

        // Read the file content
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        assertTrue(content.contains("to: john@example.com"));
        assertTrue(content.contains("Hello john_doe, " + message));
    }

    @Test
    @DisplayName("Test notify() handles IOException gracefully")
    void testNotifyHandlesIOException() {
        // Try writing to an invalid path by changing log file via reflection
        try {
            var field = EmailNotifier.class.getDeclaredField("LOG_FILE");
            field.setAccessible(true);
            field.set(notifier, "/invalid/path/emails.txt"); // force IO error
        } catch (Exception e) {
            fail("Reflection setup failed");
        }

        assertDoesNotThrow(() -> notifier.notify(user, "Test message"));
    }
}
