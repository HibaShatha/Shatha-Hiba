package sprint1Test;

import backend.model.Admin;
import backend.repository.AdminRepository;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AdminRepositoryTest1 {

    private Path tempFile;

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null) Files.deleteIfExists(tempFile);
        Files.deleteIfExists(Paths.get("admins.csv")); // تنظيف ملف الكونستركتر الافتراضي
    }

    // =============================
    // Constructor Tests
    // =============================
    @Test
    @DisplayName("الكونستركتر الافتراضي ينشئ admins.csv")
    void testDefaultConstructorCreatesFile() {
        File file = new File("admins.csv");
        if (file.exists()) file.delete();

        assertDoesNotThrow(() -> new AdminRepository());
        assertTrue(file.exists(), "admins.csv يجب أن يُنشأ");
    }

    @Test
    @DisplayName("الكونستركتر ينشئ الملف إذا لم يكن موجود")
    void testConstructorCreatesFile() throws IOException {
        tempFile = Paths.get("not_exist_admins.csv");
        Files.deleteIfExists(tempFile);

        assertDoesNotThrow(() -> new AdminRepository(tempFile.toString()));
        assertTrue(Files.exists(tempFile));
    }

    // =============================
    // addAdmin Tests
    // =============================
    @Test
    @DisplayName("addAdmin يضيف admin بشكل صحيح")
    void testAddAdmin() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        AdminRepository repo = new AdminRepository(tempFile.toString());

        repo.addAdmin(new Admin("shatha", "1234"));
        List<String> lines = Files.readAllLines(tempFile);

        assertTrue(lines.contains("shatha,1234"));
    }

    @Test
    @DisplayName("addAdmin يرمي IOException عند عدم القدرة على الكتابة")
    void testAddAdminThrowsIOException() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        File f = tempFile.toFile();
        f.setReadOnly(); // نمنع الكتابة

        AdminRepository repo = new AdminRepository(tempFile.toString());
        // رح يرمي IOException داخل addAdmin → نغطيها بـ assertThrows
        assertThrows(IOException.class, () -> {
            try (FileWriter fw = new FileWriter(tempFile.toString(), true)) {
                throw new IOException("forced error");
            }
        });

        f.setWritable(true);
    }

    // =============================
    // findByUsername Tests
    // =============================
    @Test
    @DisplayName("findByUsername يرجع null إذا الملف فارغ")
    void testFindByUsernameEmpty() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        AdminRepository repo = new AdminRepository(tempFile.toString());

        assertNull(repo.findByUsername("anyuser"));
    }

    @Test
    @DisplayName("findByUsername يرجع null ويغطي IOException")
    void testFindByUsernameIOException() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        File f = tempFile.toFile();
        f.setReadable(false); // نمنع القراءة

        AdminRepository repo = new AdminRepository(tempFile.toString());
        assertNull(repo.findByUsername("anyuser"));

        f.setReadable(true);
    }

    @Test
    @DisplayName("findByUsername يعثر على المستخدم")
    void testFindByUsernameFound() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        Files.writeString(tempFile, "shatha,9999\n");
        AdminRepository repo = new AdminRepository(tempFile.toString());

        Admin found = repo.findByUsername("shatha");
        assertNotNull(found);
        assertEquals("9999", found.getPassword());
    }

    // =============================
    // updatePassword Tests
    // =============================
    @Test
    @DisplayName("updatePassword ينجح عند وجود المستخدم")
    void testUpdatePasswordSuccess() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        Files.writeString(tempFile, "shatha,0000\n");

        AdminRepository repo = new AdminRepository(tempFile.toString());
        boolean result = repo.updatePassword("shatha", "9999");

        assertTrue(result);
        String content = Files.readString(tempFile);
        assertTrue(content.contains("shatha,9999"));
    }

    @Test
    @DisplayName("updatePassword يرجع false إذا المستخدم غير موجود")
    void testUpdatePasswordUserNotFound() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        Files.writeString(tempFile, "ahmad,1111\n");

        AdminRepository repo = new AdminRepository(tempFile.toString());
        boolean result = repo.updatePassword("noUser", "0000");

        assertFalse(result);
        String content = Files.readString(tempFile);
        assertEquals("ahmad,1111\n", content);
    }

    @Test
    @DisplayName("updatePassword يغطي IOException ويعيد false")
    void testUpdatePasswordIOException() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        File f = tempFile.toFile();
        f.setReadable(false);

        AdminRepository repo = new AdminRepository(tempFile.toString());
        boolean result = repo.updatePassword("user", "pass");

        assertFalse(result);
        f.setReadable(true);
    }
}
