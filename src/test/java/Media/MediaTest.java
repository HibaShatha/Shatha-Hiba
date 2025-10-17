package Media;
import backend.model.Media;
import backend.model.fine.FineStrategy;
import org.junit.jupiter.api.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MediaTest {

    static class TestMedia extends Media {
        public TestMedia(String title, String author, String isbn, FineStrategy fineStrategy) {
            super(title, author, isbn, fineStrategy);
        }

        @Override
        public void borrow(String username) {
            this.borrowed = true;
            this.borrowerUsername = username;
            this.dueDate = LocalDate.now().plusDays(7);
        }

        @Override
        public void returned() {
            this.borrowed = false;
            this.borrowerUsername = null;
            this.dueDate = null;
        }

        @Override
        public boolean isOverdue() {
            if (dueDate == null) return false;
            return LocalDate.now().isAfter(dueDate);
        }

        @Override
        public double calculateFine() {
            if (!isOverdue()) return 0.0;
            return 5.0; // قيمة ثابتة للفحص
        }
    }

    private Media media;

    @BeforeEach
    void setUp() {
        media = new TestMedia("Title1", "Author1", "ISBN1", null);
    }

    @Test
    @DisplayName("اختبار getters الأساسية")
    void testGetters() {
        assertEquals("Title1", media.getTitle());
        assertEquals("Author1", media.getAuthor());
        assertEquals("ISBN1", media.getIsbn());
        assertFalse(media.isBorrowed());
        assertNull(media.getDueDate());
        assertNull(media.getBorrowerUsername());
        assertEquals("TestMedia", media.getMediaType());
    }

    @Test
    @DisplayName("اختبار borrow و returned")
    void testBorrowAndReturn() {
        media.borrow("user1");
        assertTrue(media.isBorrowed());
        assertEquals("user1", media.getBorrowerUsername());
        assertNotNull(media.getDueDate());

        media.returned();
        assertFalse(media.isBorrowed());
        assertNull(media.getBorrowerUsername());
        assertNull(media.getDueDate());
    }

    @Test
    @DisplayName("اختبار isOverdue و calculateFine")
    void testOverdueAndFine() {
        assertFalse(media.isOverdue());
        assertEquals(0.0, media.calculateFine());

        // جعل dueDate في الماضي
        media.setDueDate(LocalDate.now().minusDays(1));
        assertTrue(media.isOverdue());
        assertEquals(5.0, media.calculateFine());
    }

    @Test
    @DisplayName("اختبار setDueDate")
    void testSetDueDate() {
        LocalDate date = LocalDate.now().plusDays(3);
        media.setDueDate(date);
        assertEquals(date, media.getDueDate());
    }
}