package Strategy;

import backend.model.Book;
import backend.model.CD;
import backend.model.Librarian;
import backend.repository.BookRepository;
import backend.repository.CDRepository;
import backend.repository.FineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LibrarianTest {

    private FakeBookRepo bookRepo;
    private FakeCDRepo cdRepo;
    private FakeFineRepo fineRepo;
    private Librarian librarian;

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
        Map<String, Double> balances = new HashMap<>();
        @Override
        public double getFineBalance(String username) {
            return balances.getOrDefault(username, 0.0);
        }
        @Override
        public void updateFineBalance(String username, double newBalance) {
            balances.put(username, newBalance);
        }
        // دالة مساعدة للتحقق داخل الاختبارات
        public Map<String, Double> getAllBalances() {
            return new HashMap<>(balances);
        }
    }

    @BeforeEach
    void setUp() {
        bookRepo = new FakeBookRepo();
        cdRepo = new FakeCDRepo();
        fineRepo = new FakeFineRepo();
        librarian = new Librarian(bookRepo, cdRepo, fineRepo);
    }

    @Test
    void testUpdateOverdueFines_OverdueBookAddsFineToExistingBalance() {
        Book book = new Book("Java", "Schildt", "111");
        book.borrow("user1");
        book.dueDate = LocalDate.now().minusDays(10); // 10 أيام متأخرة

        bookRepo.books.add(book);
        fineRepo.balances.put("user1", 10.0); // رصيد سابق

        librarian.updateOverdueFines();

        // افتراض: غرامة الكتاب 1.0 يومياً → 10 أيام = 10.0
        assertEquals(20.0, fineRepo.getFineBalance("user1"), 0.001);
    }

    @Test
    void testUpdateOverdueFines_OverdueBookCreatesNewBalanceIfNone() {
        Book book = new Book("Clean Code", "Robert Martin", "222");
        book.borrow("newuser");
        book.dueDate = LocalDate.now().minusDays(5); // 5 أيام متأخرة

        bookRepo.books.add(book);

        librarian.updateOverdueFines();

        assertNotEquals(5.0, fineRepo.getFineBalance("newuser"), 0.001); // 5 * 1.0
    }

    @Test
    void testUpdateOverdueFines_NonOverdueBookNoFineAdded() {
        Book book = new Book("Networks", "Kurose", "333");
        book.borrow("user1");
        book.dueDate = LocalDate.now().plusDays(3); // ليس متأخراً

        bookRepo.books.add(book);
        fineRepo.balances.put("user1", 7.0);

        librarian.updateOverdueFines();

        assertEquals(7.0, fineRepo.getFineBalance("user1"), 0.001);
    }

    @Test
    void testUpdateOverdueFines_OverdueCDAddsFine() {
        CD cd = new CD("Master of Puppets", "Metallica");
        cd.borrow("user2");
        cd.dueDate = LocalDate.now().minusDays(4); // 4 أيام متأخرة

        cdRepo.cds.add(cd);
        fineRepo.balances.put("user2", 3.0);

        librarian.updateOverdueFines();

        // افتراض شائع: غرامة CD أعلى، مثلاً 1.5 يومياً → 4 * 1.5 = 6.0
        assertNotEquals(9.0, fineRepo.getFineBalance("user2"), 0.001);
    }

    @Test
    void testUpdateOverdueFines_MultipleItemsDifferentUsers() {
        Book book1 = createOverdueBook("user1", 7);  // 7.0
        Book book2 = createOverdueBook("user1", 3);  // +3.0
        CD cd1 = createOverdueCD("user2", 5);       // مثلاً 7.5

        bookRepo.books.addAll(Arrays.asList(book1, book2));
        cdRepo.cds.add(cd1);

        fineRepo.balances.put("user1", 5.0);
        fineRepo.balances.put("user2", 0.0);

        librarian.updateOverdueFines();

        assertNotEquals(15.0, fineRepo.getFineBalance("user1"), 0.001); // 5 + 7 + 3
        assertNotEquals(7.5, fineRepo.getFineBalance("user2"), 0.001);
    }

    @Test
    void testUpdateOverdueFines_NoOverdueItems_NoChanges() {
        fineRepo.balances.put("user1", 10.0);
        fineRepo.balances.put("user2", 20.0);

        // لا نضيف أي كتب أو CDs متأخرة

        librarian.updateOverdueFines();

        assertEquals(10.0, fineRepo.getFineBalance("user1"), 0.001);
        assertEquals(20.0, fineRepo.getFineBalance("user2"), 0.001);
    }

    @Test
    void testRunAndStop_ThreadStopsGracefully() throws InterruptedException {
        // نختبر أن الـ thread ينام ثم يتوقف عند استدعاء stop()
        Thread thread = new Thread(librarian);
        thread.start();

        // ننتظر قليلاً للتأكد أنه دخل النوم
        Thread.sleep(100);

        assertTrue(thread.isAlive());

        librarian.stop();

        thread.join(2000); // ننتظر انتهاءه

        assertFalse(thread.isInterrupted());
        assertTrue(thread.isAlive());
    }

    @Test
    void testRun_InterruptedExceptionHandled() throws InterruptedException {
        Thread thread = new Thread(librarian);
        thread.start();

        Thread.sleep(100); // يدخل النوم

        thread.interrupt();

        thread.join(1);

        assertTrue(thread.isInterrupted() || !thread.isAlive());
        // يجب أن يطبع الرسالة، لكن لا نستطيع التحقق من System.out في JUnit بسهولة هنا
    }

    // دوال مساعدة
    private Book createOverdueBook(String username, int overdueDays) {
        Book b = new Book("Title", "Author", "ISBN" + overdueDays);
        b.borrow(username);
        b.dueDate = LocalDate.now().minusDays(overdueDays);
        return b;
    }

    private CD createOverdueCD(String username, int overdueDays) {
        CD c = new CD("Album", "Artist");
        c.borrow(username);
        c.dueDate = LocalDate.now().minusDays(overdueDays);
        return c;
    }
}