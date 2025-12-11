package sprint2Test;

import backend.model.User;
import backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;
    private Path usersFilePath;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // إنشاء مسار الملف المؤقت داخل المجلد المؤقت
        usersFilePath = tempDir.resolve("users.csv");
        userRepository = new UserRepository(usersFilePath.toString());
        // الملف يُنشأ تلقائياً في الكونستركتور، لا نحتاج لإنشائه يدوياً
    }

    @Test
    void testFileIsCreatedAutomatically() {
        // التأكد من أن الملف تم إنشاؤه تلقائياً عند إنشاء الكائن
        assertTrue(Files.exists(usersFilePath), "users.csv should be created automatically");
    }

    @Test
    void testAddUser_AppendsNewUser() throws IOException {
        User user = new User("ahmad", "secret123", "ahmad@example.com", "0599123456");

        userRepository.addUser(user);

        assertLinesMatch(
            java.util.List.of("ahmad,secret123,ahmad@example.com,0599123456"),
            Files.readAllLines(usersFilePath)
        );
    }

    @Test
    void testAddMultipleUsers() throws IOException {
        User user1 = new User("user1", "pass1", "u1@example.com", "111");
        User user2 = new User("user2", "pass2", "", "222"); // email فارغ

        userRepository.addUser(user1);
        userRepository.addUser(user2);

        assertLinesMatch(
            java.util.List.of(
                "user1,pass1,u1@example.com,111",
                "user2,pass2,,222"
            ),
            Files.readAllLines(usersFilePath)
        );
    }

    @Test
    void testFindByUsername_ExistingUserWithAllFields() throws IOException {
        writeLines(
            "hiba,hibapass,hiba@mail.com,056789",
            "ali,alipass,ali@mail.com,057890"
        );

        User found = userRepository.findByUsername("ali");

        assertNotNull(found);
        assertEquals("ali", found.getUsername());
        assertEquals("alipass", found.getPassword());
        assertEquals("ali@mail.com", found.getEmail());
        assertEquals("057890", found.getPhoneNumber());
    }

    @Test
    void testFindByUsername_ExistingUserWithMissingFields() throws IOException {
        writeLines(
            "user1,pass1,,",  // email و phone فارغان
            "user2,pass2,email2@example.com," // phone فارغ
        );

        User found = userRepository.findByUsername("user1");

        assertNotNull(found);
        assertEquals("user1", found.getUsername());
        assertEquals("pass1", found.getPassword());
        assertEquals("", found.getEmail());
        assertEquals("", found.getPhoneNumber());
    }

    @Test
    void testFindByUsername_NonExistentUser() throws IOException {
        writeLines("existing,pass123,a@b.c,123");

        User found = userRepository.findByUsername("nonexistent");

        assertNull(found);
    }

    @Test
    void testFindByUsername_EmptyFile() {
        User found = userRepository.findByUsername("anyone");
        assertNull(found);
    }

    @Test
    void testUpdatePassword_SuccessfulUpdate() throws IOException {
        writeLines(
            "user1,oldpass,user1@example.com,111",
            "user2,pass2,u2@example.com,222"
        );

        boolean updated = userRepository.updatePassword("user1", "newSecurePass!123");

        assertTrue(updated);

        assertLinesMatch(
            java.util.List.of(
                "user1,newSecurePass!123,user1@example.com,111",
                "user2,pass2,u2@example.com,222"
            ),
            Files.readAllLines(usersFilePath)
        );
    }

    @Test
    void testUpdatePassword_UserNotFound() throws IOException {
        writeLines("user1,pass1,email,phone");

        boolean updated = userRepository.updatePassword("unknown", "anything");

        assertFalse(updated);

        // الملف يجب أن يبقى كما هو
        assertLinesMatch(
            java.util.List.of("user1,pass1,email,phone"),
            Files.readAllLines(usersFilePath)
        );
    }

    @Test
    void testUpdatePassword_PreservesOtherFieldsIncludingEmpty() throws IOException {
        writeLines(
            "testuser,old,,",  // email و phone فارغان
            "other,pass,other@mail.com,999"
        );

        userRepository.updatePassword("testuser", "updatedPass");

        assertLinesMatch(
            java.util.List.of(
                "testuser,updatedPass,,",
                "other,pass,other@mail.com,999"
            ),
            Files.readAllLines(usersFilePath)
        );
    }

    @Test
    void testRemoveUser_SuccessfulRemoval() throws IOException {
        writeLines(
            "deleteMe,pass1,a@b.c,123",
            "keepMe,pass2,x@y.z,456",
            "another,pass3,,,789"
        );

        boolean removed = userRepository.removeUser("deleteMe");

        assertTrue(removed);

        assertLinesMatch(
            java.util.List.of(
                "keepMe,pass2,x@y.z,456",
                "another,pass3,,,789"
            ),
            Files.readAllLines(usersFilePath)
        );
    }

    @Test
    void testRemoveUser_UserNotFound() throws IOException {
        writeLines(
            "user1,pass1,email,phone",
            "user2,pass2,e2@p.com,222"
        );

        boolean removed = userRepository.removeUser("nonexistent");

        assertFalse(removed);

        // الملف لا يتغير
        assertLinesMatch(
            java.util.List.of(
                "user1,pass1,email,phone",
                "user2,pass2,e2@p.com,222"
            ),
            Files.readAllLines(usersFilePath)
        );
    }

    @Test
    void testRemoveUser_RemoveLastRemainingUser() throws IOException {
        writeLines("onlyuser,pass123,email,phone");

        boolean removed = userRepository.removeUser("onlyuser");

        assertTrue(removed);
        assertTrue(Files.exists(usersFilePath), "File should still exist even if empty");
        assertTrue(Files.readAllLines(usersFilePath).isEmpty(), "File should be empty after removing the only user");
    }

    @Test
    void testRemoveUser_EmptyFile() {
        boolean removed = userRepository.removeUser("anyone");
        assertFalse(removed);
    }
    
 // لا حاجة لتغيير شيء إلا إذا أردنا إضافة اختبار للـ formatUser helper
    @Test
    void testAddUser_UsesFormatUser() throws IOException {
        User user = new User("formatTest", "pass123", "f@example.com", "0999");
        userRepository.addUser(user);

        assertLinesMatch(
            java.util.List.of("formatTest,pass123,f@example.com,0999"),
            Files.readAllLines(usersFilePath)
        );
    }


    // دوال مساعدة لتسهيل الكتابة والتحقق
    private void writeLines(String... lines) throws IOException {
        Files.write(usersFilePath, java.util.List.of(lines));
    }
    
    
}