package Service;

import backend.service.EmailService;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    private EmailService service;
    private File tempEmailFile;

    @BeforeEach
    void setUp() throws IOException {
        // إنشاء ملف مؤقت لكل تيست
        tempEmailFile = File.createTempFile("emails", ".txt");
        tempEmailFile.deleteOnExit();

        // كتابة بيانات تجريبية
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempEmailFile))) {
            bw.write("to: john@example.com | message: Hello John!");
            bw.newLine();
            bw.write("to: alice@example.com | message: Welcome Alice!");
            bw.newLine();
        }

        service = new EmailService(tempEmailFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (tempEmailFile.exists()) tempEmailFile.delete();
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
        // حذف الملف قبل استدعاء الميثود
        tempEmailFile.delete();

        assertDoesNotThrow(() -> {
            List<String> messages = service.getMessagesForEmail("john@example.com");
            assertTrue(messages.isEmpty());
        });
    }

    @Test
    @DisplayName("Test getMessagesForEmail() handles IOException gracefully (invalid path)")
    void testGetMessagesForEmailWithInvalidPath() {
        EmailService brokenService = new EmailService("Z:/nonexistent/folder/file.txt");

        assertDoesNotThrow(() -> {
            List<String> messages = brokenService.getMessagesForEmail("john@example.com");
            assertTrue(messages.isEmpty());
        });
    }
}
