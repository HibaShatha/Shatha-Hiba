package Service;

import backend.model.Book;
import backend.model.User;
import backend.service.MediaService;
import backend.service.Observer;
import backend.service.ReminderService;
import backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReminderServiceTest {

    private User user;
    private Book book;
    private ReminderService reminderService;
    private boolean[] emailCalled;
    private boolean[] smsCalled;

    @BeforeEach
    void setUp() {
        user = new User("john", "1234", "john@test.com", "1234567890");
        book = new Book("Book1", "Author1", "ISBN1");
        book.borrow("john");
        book.setDueDate(LocalDate.now().minusDays(1)); // جعله متأخر

        // Counters لتأكيد notify
        emailCalled = new boolean[]{false};
        smsCalled = new boolean[]{false};

        // MediaService وهمي
        MediaService mediaService = new MediaService(null) {
            @Override
            public List<Book> getOverdueBooks() {
                return List.of(book);
            }
        };

        // UserService وهمي
        UserService userService = new UserService() {
            @Override
            public User findByUsername(String username) {
                return username.equals("john") ? user : null;
            }
        };

        // ReminderService subclass لإزالة observers الافتراضية
        reminderService = new ReminderService(mediaService, userService) {
            {
                observers.clear(); // نزيل observers الافتراضية
                observers.add((u, msg) -> {
                    emailCalled[0] = true;
                    assertEquals(user, u);
                    assertTrue(msg.contains("overdue book"));
                });
                observers.add((u, msg) -> {
                    smsCalled[0] = true;
                    assertEquals(user, u);
                    assertTrue(msg.contains("overdue book"));
                });
            }
        };
    }

    @Test
    void testSendRemindersObserversCalled() {
        reminderService.sendReminders();
        assertTrue(emailCalled[0], "Email observer should have been notified");
        assertTrue(smsCalled[0], "SMS observer should have been notified");
    }

    @Test
    void testSendRemindersNoOverdueBooks() {
        // كتاب غير متأخر
        book.setDueDate(LocalDate.now().plusDays(1));

        // Counters
        emailCalled[0] = false;
        smsCalled[0] = false;

        // MediaService وهمي جديد يعكس الحالة الحالية
        reminderService = new ReminderService(
            new MediaService(null) {
                @Override
                public List<Book> getOverdueBooks() {
                    // الكتاب الآن غير متأخر → يجب إرجاع قائمة فارغة
                    return List.of(); 
                }
            },
            new UserService() {
                @Override
                public User findByUsername(String username) {
                    return username.equals("john") ? user : null;
                }
            }
        ) {
            {
                observers.clear(); // نزيل observers الافتراضية
                observers.add((u, msg) -> emailCalled[0] = true);
                observers.add((u, msg) -> smsCalled[0] = true);
            }
        };

        reminderService.sendReminders();

        assertFalse(emailCalled[0], "Email observer should NOT have been notified");
        assertFalse(smsCalled[0], "SMS observer should NOT have been notified");
    }

    @Test
    void testSendRemindersUserNotFound() {
        // غير username ليكون غير موجود
        book.borrow("unknownUser");

        emailCalled[0] = false;
        smsCalled[0] = false;

        reminderService.sendReminders();

        // observers لا يتم استدعاؤهم إذا user غير موجود
        assertFalse(emailCalled[0]);
        assertFalse(smsCalled[0]);
    }
}
