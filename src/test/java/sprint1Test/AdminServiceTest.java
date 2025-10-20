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
    private AdminService adminService;
    private AdminRepository repo;
    UserRepository userRepo;
    MediaService mediaService;
    File tempUserFile;
    MediaService bookService;

    @BeforeEach
    void setup() throws IOException {
        tempUserFile = new File("users.csv");
        if (tempUserFile.exists()) tempUserFile.delete();

        adminService = new AdminService();
        userRepo = new UserRepository();
        mediaService = new MediaService(adminService);

        adminService.createAccount("admin", "1234");
        adminService.login("admin", "1234");
        


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
        adminService.logout(); 
        }

    @BeforeEach
    void setUp() {
        adminService = new AdminService();
        repo = new AdminRepository();

        repo.addAdmin(new Admin("shatha", "1234"));
        adminService = new AdminService();

        adminService.createAccount("admin", "1234");
    }

 
  
    @Test
    @DisplayName("isLoggedInUsername: when addmin loggin")
    void testISLoggedInUsername() {
        boolean loggedIn = adminService.login("shatha", "1234");
        assertTrue(loggedIn, "Admin should be able to log in");

        String username = adminService.getLoggedInUsername();
        assertEquals("shatha", username);
    }
    
    @Test
    @DisplayName("isLoggedInUsername: when admin doesn't log in")
    void testISLoggedInUsernameWhenNoAdminLoggedIn() {
        adminService.logout(); // نتأكد أنه ما في admin مسجل دخول
        String username = adminService.getLoggedInUsername();
        assertNull(username, "Should return null when no admin is logged in");
    }

    

    @Test
    @DisplayName("getLoggedInUsername: when admin is logged in")
    void testGetLoggedInUsernameWhenLoggedIn() {
        boolean loginResult = adminService.login("admin", "1234");
        assertTrue(loginResult, "Admin should log in successfully");

        String username = adminService.getLoggedInUsername();
        assertEquals("admin", username, "Logged in username should match");
    }

    @Test
    @DisplayName("getLoggedInUsername: when no admin is logged in")
    void testGetLoggedInUsernameWhenNotLoggedIn() {
        AdminService freshService = new AdminService(); // instance جديد
        String username = freshService.getLoggedInUsername();
        assertNull(username, "Username should be null when no admin is logged in");
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
    @DisplayName("unregisterUser: user has active loans (covers System.out)")
    void testUnregisterUserWithActiveLoans() {
        adminService.login("admin", "1234");
        userRepo.addUser(new User("userLoan", "pass", "email", "123"));

        Book borrowedBook = new Book("BorrowedBook", "Author", "1");
        borrowedBook.borrow("userLoan");

        // نعمل Fake MediaService يعيد قائمة فيها الكتاب المستعار
        MediaService fakeMedia = new MediaService(adminService) {
            @Override
            public java.util.List<Book> getAllBooks() {
                return java.util.List.of(borrowedBook);
            }
        };

        assertDoesNotThrow(() -> adminService.unregisterUser("userLoan", userRepo, fakeMedia));

        // نتأكد أنه ما انمسح المستخدم لأنه عنده كتاب مستعار
        assertNotNull(userRepo.findByUsername("userLoan"), "User should not be unregistered because they have active loans");
    }


    @Test
    @DisplayName("unregisterUser: user has outstanding fines")
    void testUnregisterUserWithFineBalance() {
        adminService.login("admin", "1234");
        User user = new User("userFine", "pass", "email", "123");
        userRepo.addUser(user);

        // نحط fine للمستخدم يدويًا
        Book book = new Book("Book2", "Auth", "2");
        mediaService.getAllBooks().add(book);

        // نعمل mock بسيط بالاعتماد على الدالة الأصلية
        MediaService spyMediaService = new MediaService(adminService) {
            @Override
            public double getUserFineBalance(String username) {
                if (username.equals("userFine")) return 10.0;
                return 0.0;
            }
        };

        assertDoesNotThrow(() -> adminService.unregisterUser("userFine", userRepo, spyMediaService));
        assertNotNull(userRepo.findByUsername("userFine"), "User should not be unregistered due to unpaid fine");
    }


    @Test
    @DisplayName("unregisterUser: removeUser returns false (error case)")
    void testUnregisterUserRemoveFails() {
        adminService.login("admin", "1234");

        UserRepository fakeRepo = new UserRepository() {
            @Override
            public boolean removeUser(String username) {
                return false; // نحاكي فشل حذف المستخدم
            }

            @Override
            public User findByUsername(String username) {
                return new User(username, "pass", "mail", "id");
            }
        };

        assertDoesNotThrow(() -> adminService.unregisterUser("fakeUser", fakeRepo, mediaService));
    }



    @Test
    @DisplayName("unregisterUser")
    void testUnregisterUser() {
        adminService.logout();
        assertDoesNotThrow(() -> adminService.unregisterUser("user1", userRepo, mediaService));
        adminService.login("admin", "1234");

        assertDoesNotThrow(() -> adminService.unregisterUser("noUser", userRepo, mediaService));

        Book book = new Book("Book1", "Author", "1");
        book.borrow("user1");
        
        mediaService.getAllBooks().add(book); 
        assertDoesNotThrow(() -> adminService.unregisterUser("user1", userRepo, mediaService));
        book.returned();
        
        assertDoesNotThrow(() -> {
            double old = mediaService.getUserFineBalance("user1");
            mediaService.getUserFineBalance("user1"); 
        });

        assertDoesNotThrow(() -> adminService.unregisterUser("user1", userRepo, mediaService));
    }
    
    
}
