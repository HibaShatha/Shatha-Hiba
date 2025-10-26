package Service;

import backend.model.User;
import backend.service.EmailNotifier;
import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmailNotifierTest {

    private EmailNotifier notifier;
    private User user;
    private File tempLogFile;

    @BeforeEach
    void setUp() throws IOException {
        notifier = new EmailNotifier();
        user = new User("john_doe", "pass123", "john@example.com", "1234567890");

        // Create a temporary file for logging emails
        tempLogFile = File.createTempFile("emails", ".txt");
        tempLogFile.deleteOnExit(); // clean up automatically after JVM exits
        notifier.LOG_FILE = tempLogFile.getAbsolutePath();
    }

    @Test
    @DisplayName("Test notify() writes correct log entry")
    void testNotifyWritesToFile() throws IOException {
        String message = "Your account has been updated.";
        notifier.notify(user, message);

        // Verify the file exists
        assertTrue(tempLogFile.exists(), "Temporary log file should exist");

        // Read the file content
        String content = new String(java.nio.file.Files.readAllBytes(tempLogFile.toPath()));
        assertTrue(content.contains("to: " + user.getEmail()));
        assertTrue(content.contains("Hello " + user.getUsername() + ", " + message));
    }

    @Test
    @DisplayName("Test notify() handles IOException gracefully and stores the exception message")
    void testNotifyHandlesIOExceptionAndStoresMessage() {
        final String simulatedMessage = "Simulated I/O failure";

        // Subclass that forces an IOException
        EmailNotifier badNotifier = new EmailNotifier() {
            @Override
            protected void logMockEmail(String email, String message) throws IOException {
                throw new IOException(simulatedMessage);
            }
        };

        // Call notify - should not throw
        assertDoesNotThrow(() -> badNotifier.notify(user, "Test message"));

        // Check that the exception message is stored
        assertEquals(simulatedMessage, badNotifier.getLastErrorMessage(),
                     "The notifier should store the IOException message in lastErrorMessage");
    }
}
