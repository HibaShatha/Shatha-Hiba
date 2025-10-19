package Service;

import backend.service.EmailService;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    private static final String EMAIL_FILE = "emails.csv";
    private EmailService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new EmailService();

        // Create sample file before each test
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EMAIL_FILE))) {
            bw.write("to: john@example.com | message: Hello John!");
            bw.newLine();
            bw.write("to: alice@example.com | message: Welcome Alice!");
            bw.newLine();
        }
    }

    @AfterEach
    void tearDown() {
        File file = new File(EMAIL_FILE);
        if (file.exists()) file.delete();
    }

    @Test
    @DisplayName("Test getMessagesForEmail() returns messages for valid email")
    void testGetMessagesForValidEmail() {
        List<String> messages = service.getMessagesForEmail("john@example.com");

        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("Hello John!"));
    }

    @Test
    @DisplayName("Test getMessagesForEmail() returns empty list for non-existent email")
    void testGetMessagesForNonExistentEmail() {
        List<String> messages = service.getMessagesForEmail("unknown@example.com");

        assertTrue(messages.isEmpty(), "Expected no messages for this email");
    }

    @Test
    @DisplayName("Test getMessagesForEmail() handles missing file gracefully")
    void testGetMessagesForMissingFile() {
        // Delete file before calling method
        File file = new File(EMAIL_FILE);
        file.delete();

        assertDoesNotThrow(() -> {
            List<String> messages = service.getMessagesForEmail("john@example.com");
            assertTrue(messages.isEmpty());
        });
    }
}
