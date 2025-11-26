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
        Files.deleteIfExists(Paths.get("files/admins.csv"));
    }

    @Test
    @DisplayName("الكونستركتر الافتراضي ينشئ files/admins.csv")
    void testDefaultConstructorCreatesFile() {
        File file = new File("files/admins.csv");
        if (file.exists()) file.delete();

        assertDoesNotThrow(() -> new AdminRepository());
        assertTrue(file.exists(), "files/admins.csv يجب أن يُنشأ");
    }

    @Test
    @DisplayName("الكونستركتر ينشئ الملف إذا لم يكن موجود")
    void testConstructorCreatesFile() throws IOException {
        tempFile = Paths.get("test_admins_file.csv");
        Files.deleteIfExists(tempFile);

        assertDoesNotThrow(() -> new AdminRepository(tempFile.toString()));
        assertTrue(Files.exists(tempFile));
    }

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
        f.setReadable(false);

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

    @Test
    @DisplayName("updatePassword ينجح عند وجود المستخدم")
    void testUpdatePasswordSuccess() throws IOException {
        tempFile = Files.createTempFile("admins", ".csv");
        Files.writeString(tempFile, "shatha,0000\n");

        AdminRepository repo = new AdminRepository(tempFile.toString());
        boolean result = repo.updatePassword("shatha", "9999");

        assertTrue(result);
        String content = Files.readString(tempFile).replace("\r\n", "\n").trim();
        assertEquals("shatha,9999", content);

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
