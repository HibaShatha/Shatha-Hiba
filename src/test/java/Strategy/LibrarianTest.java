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

    // ====== Test updateOverdueFines ======
    @Test
    void testUpdateOverdueFines_AllCases() {
        FakeBookRepo bookRepo = new FakeBookRepo();
        FakeCDRepo cdRepo = new FakeCDRepo();
        FakeFineRepo fineRepo = new FakeFineRepo();

        // كتب/CDs متأخرة
        Book overdueBook = new Book("Java", "Schildt", "111");
        overdueBook.borrow("user1");
        overdueBook.dueDate = LocalDate.now().minusDays(5);

        CD overdueCD = new CD("Metallica", "Metallica");
        overdueCD.borrow("user2");
        overdueCD.dueDate = LocalDate.now().minusDays(3);

        // كتب/CDs غير متأخرة
        Book onTimeBook = new Book("Python", "Lutz", "222");
        onTimeBook.borrow("user3");
        onTimeBook.dueDate = LocalDate.now().plusDays(2);

        CD onTimeCD = new CD("Linkin Park", "LP");
        onTimeCD.borrow("user4");
        onTimeCD.dueDate = LocalDate.now().plusDays(1);

        // كتب/CDs بدون مستعير
        Book unborrowedBook = new Book("C#", "Albahari", "333");
        CD unborrowedCD = new CD("Coldplay", "Parachutes");

        bookRepo.books.addAll(Arrays.asList(overdueBook, onTimeBook, unborrowedBook));
        cdRepo.cds.addAll(Arrays.asList(overdueCD, onTimeCD, unborrowedCD));

        fineRepo.balance.put("user1", 0.0);
        fineRepo.balance.put("user2", 0.0);
        fineRepo.balance.put("user3", 0.0);
        fineRepo.balance.put("user4", 0.0);

        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);

        librarian.updateOverdueFines();

        assertTrue(fineRepo.getFineBalance("user1") > 0.0);
        assertTrue(fineRepo.getFineBalance("user2") > 0.0);
        assertEquals(0.0, fineRepo.getFineBalance("user3"));
        assertEquals(0.0, fineRepo.getFineBalance("user4"));
    }

    // ====== Test Empty Repos ======
    @Test
    void testUpdateOverdueFines_EmptyRepos() {
        FakeBookRepo bookRepo = new FakeBookRepo();
        FakeCDRepo cdRepo = new FakeCDRepo();
        FakeFineRepo fineRepo = new FakeFineRepo();

        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);
        assertDoesNotThrow(librarian::updateOverdueFines);
    }

    // ====== Test runDailyFineUpdate loop in Thread ======
    @Test
    void testRunDailyFineUpdateThread() throws InterruptedException {
        FakeBookRepo bookRepo = new FakeBookRepo();
        FakeCDRepo cdRepo = new FakeCDRepo();
        FakeFineRepo fineRepo = new FakeFineRepo();

        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);

        Thread thread = new Thread(librarian);
        thread.start();


        librarian.stop();
        thread.join();

        assertFalse(thread.isAlive());
    }

    // ====== Directly call the method for coverage ======
    @Test
    void testRunDailyFineUpdateMethodDirectly() {
        FakeBookRepo bookRepo = new FakeBookRepo();
        FakeCDRepo cdRepo = new FakeCDRepo();
        FakeFineRepo fineRepo = new FakeFineRepo();

        Book overdueBook = new Book("Java", "Schildt", "111");
        overdueBook.borrow("user1");
        overdueBook.dueDate = LocalDate.now().minusDays(2);

        CD overdueCD = new CD("Metallica", "Metallica");
        overdueCD.borrow("user2");
        overdueCD.dueDate = LocalDate.now().minusDays(1);

        bookRepo.books.add(overdueBook);
        cdRepo.cds.add(overdueCD);

        fineRepo.balance.put("user1", 0.0);
        fineRepo.balance.put("user2", 0.0);

        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);

        // استدعاء الميثود الجديدة مباشرة
        librarian.runDailyFineUpdateIterationForTest();

        assertTrue(fineRepo.getFineBalance("user1") > 0.0);
        assertTrue(fineRepo.getFineBalance("user2") > 0.0);
    }
}
