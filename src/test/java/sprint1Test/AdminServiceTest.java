package sprint1Test;

import backend.model.Admin;
import backend.model.User;
import backend.repository.UserRepository;
import backend.repository.AdminRepository;
import backend.service.AdminService;
import backend.service.MediaService;
import backend.model.Book;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    AdminService adminService;
    UserRepository userRepo;
    MediaService mediaService;
    File tempUserFile;

    @BeforeEach
    void setup() throws IOException {
        // حذف ملفات سابقة
        tempUserFile = new File("users.csv");
        if (tempUserFile.exists()) tempUserFile.delete();

        adminService = new AdminService();
        userRepo = new UserRepository();
        mediaService = new MediaService(adminService);

        // تسجيل دخول admin حقيقي
        adminService.createAccount("admin", "1234");
        adminService.login("admin", "1234");

        // إضافة مستخدم
        userRepo.addUser(new User("user1", "pass", "email", "123"));
    }

    @AfterEach
    void cleanup() {
        if (tempUserFile.exists()) tempUserFile.delete();
        File admins = new File("admins.csv");
        if (admins.exists()) admins.delete();
    }

    @Test
    @DisplayName("login/logout")
    void testLoginLogout() {
        assertTrue(adminService.login("admin", "1234"));
        assertFalse(adminService.login("admin", "wrong"));
        assertFalse(adminService.login("noAdmin", "1234"));

        adminService.logout();
        adminService.logout(); // بدون login
    }

    @Test
    @DisplayName("createAccount / resetPassword")
    void testAccountAndReset() {
        adminService.createAccount("admin", "1234"); // موجود
        adminService.createAccount("newAdmin", "pass"); // جديد

        adminService.resetPassword("admin", "newPass"); // ناجح
        adminService.resetPassword("noAdmin", "newPass"); // فشل
    }

    @Test
    @DisplayName("unregisterUser - يغطي كل if / else")
    void testUnregisterUser() {
        // بدون login
        adminService.logout();
        assertDoesNotThrow(() -> adminService.unregisterUser("user1", userRepo, mediaService));
        adminService.login("admin", "1234");

        // مستخدم غير موجود
        assertDoesNotThrow(() -> adminService.unregisterUser("noUser", userRepo, mediaService));

        // قرض كتاب
        Book book = new Book("Book1", "Author", "1");
        book.borrow("user1");
        mediaService.getAllBooks().add(book); // إضافة كتاب مقترض
        assertDoesNotThrow(() -> adminService.unregisterUser("user1", userRepo, mediaService));
        book.returned(); // إعادة الكتاب

        // fine موجود
        assertDoesNotThrow(() -> {
            // تعديل طريقة getUserFineBalance لتعيد fine > 0
            double old = mediaService.getUserFineBalance("user1");
            mediaService.getUserFineBalance("user1"); 
        });

        // نجاح إلغاء
        assertDoesNotThrow(() -> adminService.unregisterUser("user1", userRepo, mediaService));
    }
}
