package Media;

import backend.model.CD;
import backend.model.fine.CDFineStrategy;
import org.junit.jupiter.api.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CDTest {

    private CD cd;

    @BeforeEach
    void setUp() {
        cd = new CD("Greatest Hits", "Famous Artist");
    }

    @Test
    @DisplayName("Test initial state after creation")
    void testInitialState() {
        assertEquals("Greatest Hits", cd.getTitle());
        assertEquals("Famous Artist", cd.getAuthor());
        assertNull(cd.getIsbn()); // CD has no ISBN
        assertFalse(cd.isBorrowed());
        assertNull(cd.getDueDate());
        assertNull(cd.getBorrowerUsername());
        assertEquals("CD", cd.getMediaType());
    }

    @Test
    @DisplayName("Test borrow method")
    void testBorrow() {
        cd.borrow("user1");
        assertTrue(cd.isBorrowed());
        assertEquals("user1", cd.getBorrowerUsername());
        assertNotNull(cd.getDueDate());

        LocalDate expectedDue = LocalDate.now().plusDays(7);
        assertEquals(expectedDue, cd.getDueDate());
    }

    @Test
    @DisplayName("Test returned method")
    void testReturned() {
        cd.borrow("user1");
        cd.returned();
        assertFalse(cd.isBorrowed());
        assertNull(cd.getBorrowerUsername());
        assertNull(cd.getDueDate());
    }

    @Test
    @DisplayName("Test isOverdue - not overdue")
    void testIsOverdueFalse() {
        cd.borrow("user1");
        assertFalse(cd.isOverdue());
    }

    @Test
    @DisplayName("Test isOverdue - overdue")
    void testIsOverdueTrue() {
        cd.borrow("user1");
        cd.setDueDate(LocalDate.now().minusDays(3));
        assertTrue(cd.isOverdue());
    }

    @Test
    @DisplayName("Test calculateFine")
    void testCalculateFine() {
        cd.borrow("user1");
        // not overdue => fine should be 0
        assertEquals(0.0, cd.calculateFine());

        // overdue => fine should be calculated
        cd.setDueDate(LocalDate.now().minusDays(5));
        double expectedFine = new CDFineStrategy().calculateFine(5);
        assertEquals(expectedFine, cd.calculateFine());
    }
}
