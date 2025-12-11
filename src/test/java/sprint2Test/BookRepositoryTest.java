package sprint2Test;

import backend.model.Book;
import backend.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest {

    private BookRepository bookRepository;
    private File booksFile;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary books.csv file
        booksFile = new File(tempDir, "books.csv");
        bookRepository = new BookRepository(booksFile.getAbsolutePath());
        // Ensure the file is created
        booksFile.createNewFile();
    }

    @Test
    void testAddBookNotBorrowed() throws IOException {
        Book book = new Book("Test Book", "Author Name", "123");
        bookRepository.addBook(book);

        try (BufferedReader br = new BufferedReader(new FileReader(booksFile))) {
            String line = br.readLine();
            assertEquals("Test Book,Author Name,123,false,,", line);
        }
    }

    @Test
    void testAddBookNotBorrowed1() {
        Book book = new Book("Test Book", "Author Name", "123");
        assertDoesNotThrow(() -> bookRepository.addBook(book));

        assertDoesNotThrow(() -> {
            try (BufferedReader br = new BufferedReader(new FileReader(booksFile))) {
                String line = br.readLine();
                assertEquals("Test Book,Author Name,123,false,,", line);
            }
        });
    }

    @Test
    void testAddBookBorrowed1() {
        Book book = new Book("Test Book", "Author Name", "123");
        book.borrow("user1");
        book.setDueDate(LocalDate.of(2025, 10, 1));

        assertDoesNotThrow(() -> bookRepository.addBook(book));

        assertDoesNotThrow(() -> {
            try (BufferedReader br = new BufferedReader(new FileReader(booksFile))) {
                String line = br.readLine();
                assertEquals("Test Book,Author Name,123,true,2025-10-01,user1", line);
            }
        });
    }

    @Test
    void testAddBookBorrowed() throws IOException {
        Book book = new Book("Test Book", "Author Name", "123");
        book.borrow("user1");
        book.setDueDate(LocalDate.of(2025, 10, 1));
        bookRepository.addBook(book);

        try (BufferedReader br = new BufferedReader(new FileReader(booksFile))) {
            String line = br.readLine();
            assertEquals("Test Book,Author Name,123,true,2025-10-01,user1", line);
        }
    }

    @Test
    void testGetAllBooksEmptyFile() {
        List<Book> books = bookRepository.getAllBooks();
        assertTrue(books.isEmpty(), "Should return empty list for empty file");
    }

    @Test
    void testGetAllBooksMultipleBooks() throws IOException {
        try (FileWriter writer = new FileWriter(booksFile)) {
            writer.write("Book1,Author1,111,true,2025-10-01,user1\n");
            writer.write("Book2,Author2,222,false,,\n");
        }

        List<Book> books = bookRepository.getAllBooks();
        assertEquals(2, books.size());
        assertEquals("Book1", books.get(0).getTitle());
        assertTrue(books.get(0).isBorrowed());
        assertEquals(LocalDate.of(2025, 10, 1), books.get(0).getDueDate());
        assertEquals("user1", books.get(0).getBorrowerUsername());

        assertEquals("Book2", books.get(1).getTitle());
        assertFalse(books.get(1).isBorrowed());
        assertNull(books.get(1).getDueDate());
        assertNull(books.get(1).getBorrowerUsername());
    }

    // تم إصلاح هذا الاختبار بالكامل
    @Test
    void testUpdateBookSuccess() throws IOException {
        // إعداد الملف بكتاب موجود
        try (FileWriter writer = new FileWriter(booksFile)) {
            writer.write("Java Programming,Author1,111,false,,\n");
        }

        Book updatedBook = new Book("Java Programming Updated", "Author1", "111");
        updatedBook.borrow("user1");
        updatedBook.setDueDate(LocalDate.of(2025, 10, 1));

        boolean result = bookRepository.updateBook(updatedBook);

        // تم التعديل: يجب أن يرجع true عند وجود الكتاب
        assertTrue(result, "Update should return true for existing book");

        // التحقق من أن الملف تم تحديثه فعليًا
        try (BufferedReader br = new BufferedReader(new FileReader(booksFile))) {
            String line = br.readLine();
            assertEquals("Java Programming Updated,Author1,111,true,2025-10-01,user1", line);
        }
    }

    // تم إصلاح هذا الاختبار أيضًا
    @Test
    void testUpdateBookSuccess1() throws IOException {
        try (FileWriter writer = new FileWriter(booksFile)) {
            writer.write("Java Programming,Author1,111,false,,\n");
        }

        Book updatedBook = new Book("Java Programming Updated", "Author1", "111");
        updatedBook.borrow("user1");
        updatedBook.setDueDate(LocalDate.of(2025, 10, 1));

        assertDoesNotThrow(() -> {
            boolean result = bookRepository.updateBook(updatedBook);
            assertTrue(result);  // تم التعديل: يجب أن يكون true
        });

        // اختياري: التحقق من المحتوى
        try (BufferedReader br = new BufferedReader(new FileReader(booksFile))) {
            String line = br.readLine();
            assertEquals("Java Programming Updated,Author1,111,true,2025-10-01,user1", line);
        }
    }

    @Test
    void testUpdateBookNotFound() throws IOException {
        try (FileWriter writer = new FileWriter(booksFile)) {
            writer.write("Java Programming,Author1,111,false,,\n");
        }

        Book updatedBook = new Book("Nonexistent Book", "Author", "999");

        boolean result = bookRepository.updateBook(updatedBook);

        assertFalse(result, "Update should return false for non-existing book");

        try (BufferedReader br = new BufferedReader(new FileReader(booksFile))) {
            String line = br.readLine();
            assertEquals("Java Programming,Author1,111,false,,", line);
        }
    }

    @Test
    void testUpdateBookNotFound1() {
        Book updatedBook = new Book("Nonexistent Book", "Author", "999");

        assertDoesNotThrow(() -> {
            boolean result = bookRepository.updateBook(updatedBook);
            assertFalse(result);
        });
    }
}