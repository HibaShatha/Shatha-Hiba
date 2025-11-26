package Media;

import backend.model.CD;
import backend.model.fine.CDFineStrategy;
import backend.repository.CDRepository;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;

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
    @DisplayName("Test isOverdue - when not borrowed but due date passed")
    void testIsOverdueNotBorrowed() {
        cd.setDueDate(LocalDate.now().minusDays(2));
        assertFalse(cd.isOverdue());
    }

    @Test
    @DisplayName("Test isOverdue - when due date is null")
    void testIsOverdueNullDueDate() {
        cd.borrow("user1");
        cd.setDueDate(null);
        assertFalse(cd.isOverdue());
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
    @Test
    @DisplayName("Test adding CD to repository without exceptions")
    void testAddCDNoException() {
        CDRepository repo = new CDRepository("files/test_cds.csv");
        CD testCD = new CD("Test Title", "Test Author");

        assertDoesNotThrow(() -> repo.addCD(testCD), "Adding CD should not throw any exception");
    }

    @Test
    @DisplayName("Test updating CD in repository without exceptions")
    void testUpdateCDNoException() {
        CDRepository repo = new CDRepository("files/test_cds.csv");
        CD testCD = new CD("Test Title", "Test Author");
        testCD.borrow("user1");

        assertDoesNotThrow(() -> repo.updateCD(testCD), "Updating CD should not throw any exception");
    }

    @Test
    @DisplayName("Test retrieving CDs without exceptions")
    void testGetAllCDsNoException() {
        CDRepository repo = new CDRepository("files/test_cds.csv");

        assertDoesNotThrow(() -> {
            List<CD> cds = repo.getAllCDs();
            assertNotNull(cds, "Returned list should not be null");
        }, "Getting all CDs should not throw any exception");
    }

}
