package Media;

import backend.model.Book;
import backend.model.fine.BookFineStrategy;
import org.junit.jupiter.api.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Effective Java", "Joshua Bloch", "ISBN1234");
    }

    @Test
    @DisplayName("Test initial getters after creation")
    void testInitialState() {
        assertEquals("Effective Java", book.getTitle());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals("ISBN1234", book.getIsbn());
        assertFalse(book.isBorrowed());
        assertNull(book.getDueDate());
        assertNull(book.getBorrowerUsername());
        assertEquals("Book", book.getMediaType());
    }

    @Test
    @DisplayName("Test borrow method")
    void testBorrow() {
        book.borrow("user1");
        assertTrue(book.isBorrowed());
        assertEquals("user1", book.getBorrowerUsername());
        assertNotNull(book.getDueDate());

        LocalDate expectedDue = LocalDate.now().plusDays(28);
        assertEquals(expectedDue, book.getDueDate());
    }

    @Test
    @DisplayName("Test returned method")
    void testReturned() {
        book.borrow("user1");
        book.returned();
        assertFalse(book.isBorrowed());
        assertNull(book.getBorrowerUsername());
        assertNull(book.getDueDate());
    }

    @Test
    @DisplayName("Test isOverdue - not overdue")
    void testIsOverdueFalse() {
        book.borrow("user1");
        assertFalse(book.isOverdue());
    }

    @Test
    @DisplayName("Test isOverdue - overdue")
    void testIsOverdueTrue() {
        book.borrow("user1");
        book.setDueDate(LocalDate.now().minusDays(5));
        assertTrue(book.isOverdue());
    }

    @Test
    @DisplayName("Test calculateFine")
    void testCalculateFine() {
        book.borrow("user1");
        // not overdue => fine should be 0
        assertEquals(0.0, book.calculateFine());

        // overdue => fine should be calculated
        book.setDueDate(LocalDate.now().minusDays(3));
        double expectedFine = new BookFineStrategy().calculateFine(3);
        assertEquals(expectedFine, book.calculateFine());
    }
}
