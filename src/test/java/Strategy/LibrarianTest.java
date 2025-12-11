package Strategy;

import backend.model.Book;
import backend.model.CD;
import backend.model.Librarian;
import backend.repository.BookRepository;
import backend.repository.CDRepository;
import backend.repository.FineRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LibrarianTest {

    static class FakeBookRepo extends BookRepository {
        List<Book> books = new ArrayList<>();
        @Override
        public List<Book> getAllBooks() { return books; }
    }

    static class FakeCDRepo extends CDRepository {
        List<CD> cds = new ArrayList<>();
        @Override
        public List<CD> getAllCDs() { return cds; }
    }

    static class FakeFineRepo extends FineRepository {
        Map<String, Double> balance = new HashMap<>();
        @Override
        public double getFineBalance(String username) {
            return balance.getOrDefault(username, 0.0);
        }
        @Override
        public void updateFineBalance(String username, double newBalance) {
            balance.put(username, newBalance);
        }
    }

    @Test
    void testUpdateOverdueFines_RealBookAndCD() {
        FakeBookRepo bookRepo = new FakeBookRepo();
        FakeCDRepo cdRepo = new FakeCDRepo();
        FakeFineRepo fineRepo = new FakeFineRepo();

        // إعداد الكتب وCDs
        Book book1 = new Book("Java", "Schildt", "111");
        book1.borrow("user1");
        book1.dueDate = LocalDate.now().minusDays(10); // متأخر

        Book book2 = new Book("Networks", "Kurose", "222");
        book2.borrow("user1");
        book2.dueDate = LocalDate.now().plusDays(5); // غير متأخر

        CD cd1 = new CD("Metallica", "Metallica");
        cd1.borrow("user2");
        cd1.dueDate = LocalDate.now().minusDays(4); // متأخر

        CD cd2 = new CD("Linkin Park", "LP");
        cd2.borrow("user3");
        cd2.dueDate = LocalDate.now().plusDays(2); // غير متأخر

        bookRepo.books.addAll(Arrays.asList(book1, book2));
        cdRepo.cds.addAll(Arrays.asList(cd1, cd2));

        fineRepo.balance.put("user1", 5.0);
        fineRepo.balance.put("user2", 2.0);

        // إنشاء Librarian
        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);

        // **استدعاء الدالة مباشرة بدون حلقة**
        librarian.updateOverdueFines(); 

        // التحقق من النتائج
        double user1New = fineRepo.getFineBalance("user1");
        double user2New = fineRepo.getFineBalance("user2");
        double user3New = fineRepo.getFineBalance("user3");

        System.out.println("user1 fine = " + user1New);
        System.out.println("user2 fine = " + user2New);
        System.out.println("user3 fine = " + user3New);

        assertTrue(user1New > 5.0); // book1 متأخر
        assertTrue(user2New > 2.0); // cd1 متأخر
        assertEquals(0.0, user3New); // cd2 ليس متأخر
    }
    
    @Test
    void testStopMethodStopsLoop() {
        FakeBookRepo bookRepo = new FakeBookRepo();
        FakeCDRepo cdRepo = new FakeCDRepo();
        FakeFineRepo fineRepo = new FakeFineRepo();

        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);

        Thread t = new Thread(librarian);
        t.start();

        // نوقف الخيط
        librarian.stop();

        try {
            t.join(200); // نعطي الخيط وقت بسيط ينهي نفسه
        } catch (InterruptedException e) {
            fail("Thread should not be interrupted");
        }

        // إذا الخيط مات → يعني stop شغال
        assertFalse(t.isAlive());
    }

}
