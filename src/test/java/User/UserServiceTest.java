package User;

import backend.model.User;
import backend.service.UserService;
import backend.repository.UserRepository;

import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private File tempFile;
    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        // إنشاء ملف مؤقت لكل تيست
        tempFile = File.createTempFile("users", ".csv");
        tempFile.deleteOnExit();

        UserRepository repo = new UserRepository(tempFile.getAbsolutePath());
        userService = new UserService(repo);
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) tempFile.delete();
    }

    @Test
    void testUserServiceOperations() {
        // إنشاء حساب جديد
        userService.createAccount("alice", "pass123", "alice@test.com", "111222333");
        User user = userService.findByUsername("alice");
        assertNotNull(user);
        assertEquals("alice", user.getUsername());

        // تسجيل الدخول
        assertTrue(userService.login("alice", "pass123"));
        assertTrue(userService.isLoggedIn());
        assertEquals("alice", userService.getLoggedInUsername());
        assertEquals("alice@test.com", userService.getLoggedInUserEmail());

        // تسجيل دخول بفشل
        assertFalse(userService.login("alice", "wrongpass"));

        // إعادة تعيين كلمة المرور
        userService.resetPassword("alice", "newpass");
        assertTrue(userService.login("alice", "newpass"));

        // إعادة تعيين كلمة مرور لاسم غير موجود
        userService.resetPassword("bob", "1234");
        assertNull(userService.getEmailByUsername("bob"));

        // محاولة إنشاء حساب بنفس الاسم
        userService.createAccount("alice", "anypass", "alice2@test.com", "000");
        assertEquals("alice@test.com", userService.getEmailByUsername("alice")); // لا يتغير

        // تسجيل الخروج
        userService.logout();
        assertFalse(userService.isLoggedIn());

        // إزالة مستخدم
        assertTrue(userService.findByUsername("alice") != null);
        assertTrue(userService.repo.removeUser("alice")); // نحتاج تعديله للوصول repo
        assertNull(userService.findByUsername("alice"));
    }
}
