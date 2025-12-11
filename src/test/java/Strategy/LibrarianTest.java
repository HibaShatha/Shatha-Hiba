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
	        public List<Book> getAllBooks() { return books; }
	    }

	    static class FakeCDRepo extends CDRepository {
	        List<CD> cds = new ArrayList<>();
	        public List<CD> getAllCDs() { return cds; }
	    }

	    static class FakeFineRepo extends FineRepository {
	        Map<String, Double> balance = new HashMap<>();
	        public double getFineBalance(String username) {
	            return balance.getOrDefault(username, 0.0);
	        }
	        public void updateFineBalance(String username, double newBalance) {
	            balance.put(username, newBalance);
	        }
	    }

	    @Test
	    void testUpdateOverdueFines_RealBookAndCD() {
	        FakeBookRepo bookRepo = new FakeBookRepo();
	        FakeCDRepo cdRepo = new FakeCDRepo();
	        FakeFineRepo fineRepo = new FakeFineRepo();

	        // we will create real Book & CD objects
	        Book book1 = new Book("Java", "Schildt", "111");
	        book1.borrow("user1");

	        // نعدل dueDate عشان يكون متأخر
	        book1.dueDate = LocalDate.now().minusDays(10); // delayed 10 days

	        Book book2 = new Book("Networks", "Kurose", "222");
	        book2.borrow("user1");
	        // غير متأخر
	        book2.dueDate = LocalDate.now().plusDays(5);


	        CD cd1 = new CD("Metallica", "Metallica");
	        cd1.borrow("user2");
	        cd1.dueDate = LocalDate.now().minusDays(4); // متأخر 4 أيام

	        CD cd2 = new CD("Linkin Park", "LP");
	        cd2.borrow("user3");
	        cd2.dueDate = LocalDate.now().plusDays(2); // غير متأخر

	        bookRepo.books.add(book1);
	        bookRepo.books.add(book2);

	        cdRepo.cds.add(cd1);
	        cdRepo.cds.add(cd2);

	        fineRepo.balance.put("user1", 5.0);
	        fineRepo.balance.put("user2", 2.0);

	        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);

	        librarian.updateOverdueFines();

	        double user1New = fineRepo.getFineBalance("user1");
	        double user2New = fineRepo.getFineBalance("user2");
	        double user3New = fineRepo.getFineBalance("user3");

	        System.out.println("user1 fine = " + user1New);
	        System.out.println("user2 fine = " + user2New);
	        System.out.println("user3 fine = " + user3New);

	        // تحقق أن الغرامة زادت بشكل صحيح
	        assertTrue(user1New > 5.0);  // book1 was overdue 10 days
	        assertTrue(user2New > 2.0);  // cd1 overdue 4 days

	        // user3 لم يتأخر
	        assertEquals(0.0, user3New);
	    }
}
