package User;

import backend.model.User;
import backend.service.UserService;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void testUserServiceOperations() {
        UserService userService = new UserService();

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

        // إعادة تعيين كلمة المرور
        userService.resetPassword("alice", "newpass");
        assertTrue(userService.login("alice", "newpass"));

        // تسجيل الخروج
        userService.logout();
        assertFalse(userService.isLoggedIn());
    }
}
