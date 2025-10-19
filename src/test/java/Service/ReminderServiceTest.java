package Service;

import backend.model.Book;
import backend.model.User;
import backend.service.MediaService;
import backend.service.Observer;
import backend.service.ReminderService;
import backend.service.UserService;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReminderServiceTest {

    @Test
    public void testSendReminders() {
        // أنشئ مستخدم وكتاب متأخر
        User user = new User("john", "1234", "john@test.com", "1234567890");
        Book book = new Book("Book1", "Author1", "ISBN1");
        book.borrow("john");
        // نجعل الكتاب متأخر
        book.setDueDate(LocalDate.now().minusDays(1));

        // MediaService وهمي لتغذية ReminderService
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

        // Counters لتأكيد ال notify
        final boolean[] emailCalled = {false};
        final boolean[] smsCalled = {false};

        ReminderService reminderService = new ReminderService(mediaService, userService) {
            @Override
            public void addObserver(Observer observer) {
                // استبدل EmailNotifier وSMSNotifier بـ anonymous classes تتأكد من الاستدعاء
                observers.add(new Observer() {
                    @Override
                    public void notify(User u, String message) {
                        emailCalled[0] = true;
                        assertEquals(user, u);
                        assertTrue(message.contains("overdue book"));
                    }
                });
                observers.add(new Observer() {
                    @Override
                    public void notify(User u, String message) {
                        smsCalled[0] = true;
                        assertEquals(user, u);
                        assertTrue(message.contains("overdue book"));
                    }
                });
            }
        };

        reminderService.sendReminders();

        assertTrue(emailCalled[0], "Email observer should have been notified");
        assertTrue(smsCalled[0], "SMS observer should have been notified");
    }
}
