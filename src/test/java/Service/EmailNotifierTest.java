package Service;

import backend.model.User;
import backend.service.EmailNotifier;
import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmailNotifierTest {

    private EmailNotifier notifier;
    private User user;

    @BeforeEach
    void setUp() {
        notifier = new EmailNotifier();
        user = new User("john_doe", "pass123", "john@example.com", "1234567890");

        // Clean up log file before each test
        File file = new File("emails.txt");
        if (file.exists()) file.delete();
    }

    @Test
    @DisplayName("Test notify() writes correct log entry")
    void testNotifyWritesToFile() throws IOException {
        String message = "Your account has been updated.";
        notifier.notify(user, message);

        // Verify the file was created
        File file = new File("emails.txt");
        assertTrue(file.exists(), "emails.txt should be created");

        // Read the file content
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        assertTrue(content.contains("to: john@example.com"));
        assertTrue(content.contains("Hello john_doe, " + message));
    }

    @Test
    @DisplayName("Test notify() handles IOException gracefully and stores the exception message")
    void testNotifyHandlesIOExceptionAndStoresMessage() {
        final String simulatedMessage = "Simulated I/O failure";

        // subclass that forces an IOException with known message
        EmailNotifier badNotifier = new EmailNotifier() {
            @Override
            protected void logMockEmail(String email, String message) throws IOException {
                throw new IOException(simulatedMessage);
            }
        };

        // call notify - should catch the IOException internally
        assertDoesNotThrow(() -> badNotifier.notify(user, "Test message"));

        // now assert that the stored lastErrorMessage equals our simulated message
        assertEquals(simulatedMessage, badNotifier.getLastErrorMessage(),
                     "The notifier should store the IOException message in lastErrorMessage");
    }
}
