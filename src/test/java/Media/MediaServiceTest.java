package Media;

import backend.model.Book;
import backend.model.CD;
import backend.service.AdminService;
import backend.service.MediaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

// AdminService وهمي
class MockAdminService extends AdminService {
    @Override
    public boolean isLoggedIn() { return true; }
    @Override
    public void createAccount(String username, String password) {}
    @Override
    public boolean login(String username, String password) { return true; }
}

// MediaService وهمي
class MockMediaService extends MediaService {
    private List<Book> books = new ArrayList<>();
    private List<CD> cds = new ArrayList<>();

    public MockMediaService(MockAdminService adminService) { super(adminService); }

    @Override
    public List<Book> getAllBooks() { return books; }
    @Override
    public void addBookWithStartingIsbn(String title, String author, int startingIsbn, int quantity) {
        int isbn = startingIsbn;
        for (int i = 0; i < quantity; i++) {
            books.add(new Book(title, author, String.valueOf(isbn++)));
        }
    }
    @Override
    public void borrowBook(String isbn, String username) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) book.borrow(username);
        }
    }
    @Override
    public void returnBook(String isbn, String username) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) book.returned();
        }
    }
    @Override
    public double getUserFineBalance(String username) { return 0; }
    @Override
    public List<Book> getOverdueBooks() { return new ArrayList<>(); }

    @Override
    public void addCD(String title, String artist) { cds.add(new CD(title, artist)); }
    @Override
    public List<CD> getAllCDs() { return cds; }
    @Override
    public void borrowCD(String title, String username) {
        for (CD cd : cds) if (cd.getTitle().equals(title)) cd.borrow(username);
    }
    @Override
    public void returnCD(String title, String username) {
        for (CD cd : cds) if (cd.getTitle().equals(title)) cd.returned();
    }
    @Override
    public List<CD> getOverdueCDs() { return new ArrayList<>(); }

	public List searchBook(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public List searchCD(String string) {
		// TODO Auto-generated method stub
		return null;
	}
}

public class MediaServiceTest {

    private MockAdminService adminService;
    private MockMediaService mediaService;

    @BeforeEach
    void setup() {
        adminService = new MockAdminService();
        mediaService = new MockMediaService(adminService);
    }

    @Test
    void testAddBooksAndBorrowReturn() {
        mediaService.addBookWithStartingIsbn("Book A", "Author A", 100, 2);
        assertEquals(2, mediaService.getAllBooks().size());

        Book b = mediaService.getAllBooks().get(0);
        mediaService.borrowBook(b.getIsbn(), "user1");
        assertTrue(b.isBorrowed());

        mediaService.returnBook(b.getIsbn(), "user1");
        assertFalse(b.isBorrowed());
    }

    @Test
    void testCDBorrowReturn() {
        mediaService.addCD("CD1", "Artist1");
        CD cd = mediaService.getAllCDs().get(0);

        mediaService.borrowCD("CD1", "user1");
        assertTrue(cd.isBorrowed());

        mediaService.returnCD("CD1", "user1");
        assertFalse(cd.isBorrowed());
    }

 
}
