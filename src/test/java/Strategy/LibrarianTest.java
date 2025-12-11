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

    // Fake Repositories
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
    void testUpdateOverdueFines_AllCases() {
        FakeBookRepo bookRepo = new FakeBookRepo();
        FakeCDRepo cdRepo = new FakeCDRepo();
        FakeFineRepo fineRepo = new FakeFineRepo();

        // حالة كتب/CDs متأخرة
        Book overdueBook = new Book("Java", "Schildt", "111");
        overdueBook.borrow("user1");
        overdueBook.dueDate = LocalDate.now().minusDays(5); // overdue 5 days

        CD overdueCD = new CD("Metallica", "Metallica");
        overdueCD.borrow("user2");
        overdueCD.dueDate = LocalDate.now().minusDays(3); // overdue 3 days

        // حالة كتب/CDs غير متأخرة
        Book onTimeBook = new Book("Python", "Lutz", "222");
        onTimeBook.borrow("user3");
        onTimeBook.dueDate = LocalDate.now().plusDays(2);

        CD onTimeCD = new CD("Linkin Park", "LP");
        onTimeCD.borrow("user4");
        onTimeCD.dueDate = LocalDate.now().plusDays(1);

        // حالة كتب/CDs بدون مستعير
        Book unborrowedBook = new Book("C#", "Albahari", "333");
        CD unborrowedCD = new CD("Coldplay", "Parachutes");

        // إضافة كل العناصر
        bookRepo.books.addAll(Arrays.asList(overdueBook, onTimeBook, unborrowedBook));
        cdRepo.cds.addAll(Arrays.asList(overdueCD, onTimeCD, unborrowedCD));

        // تهيئة الرصيد
        fineRepo.balance.put("user1", 5.0);
        fineRepo.balance.put("user2", 2.0);
        fineRepo.balance.put("user3", 0.0);
        fineRepo.balance.put("user4", 1.0);

        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);

        // تحديث الغرامات
        librarian.updateOverdueFines();

        // Assertions دقيقة
        assertTrue(fineRepo.getFineBalance("user1") > 5.0); // overdueBook
        assertTrue(fineRepo.getFineBalance("user2") > 2.0); // overdueCD
        
        assertEquals(0.0, fineRepo.getFineBalance("user3")); // onTimeBook
        assertEquals(1.0, fineRepo.getFineBalance("user4")); // onTimeCD
    }

    @Test
    void testUpdateOverdueFines_EmptyRepos() {
        FakeBookRepo bookRepo = new FakeBookRepo();
        FakeCDRepo cdRepo = new FakeCDRepo();
        FakeFineRepo fineRepo = new FakeFineRepo();

        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);

        // يجب أن لا يحدث أي استثناء حتى لو لا يوجد كتب/CDs
        assertDoesNotThrow(librarian::updateOverdueFines);
    }

    
}
