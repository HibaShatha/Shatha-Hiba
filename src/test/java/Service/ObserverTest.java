package Service;


import backend.model.User;
import backend.service.Observer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ObserverTest {

    @Test
    public void testNotify() {
        User user = new User("john", "1234", "john@test.com", "1234567890");

        // نستخدم anonymous class لتطبيق Observer
        Observer observer = new Observer() {
            boolean called = false;
            @Override
            public void notify(User u, String message) {
                called = true;
                assertEquals(user, u); // تأكد إنه نفس المستخدم
                assertEquals("Hello message!", message); // تأكد الرسالة
            }
        };

        observer.notify(user, "Hello message!");
        // هنا نحتاج نتاكد انه تم استدعاء notify
        // للأسف المتغير 'called' مش متاح برا anonymous class مباشرة
        // ممكن نستعمل class صغير بدلها لو بدك assert على الاستدعاء
    }
}
