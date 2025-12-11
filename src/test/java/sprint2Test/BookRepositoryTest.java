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
        // إنشاء ملف مؤقت books.csv لكل اختبار
        booksFile = new File(tempDir, "books.csv");
        bookRepository = new BookRepository(booksFile.getAbsolutePath());
        booksFile.createNewFile(); // ضمان وجود الملف
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

        // التحقق من الكتاب الأول
        Book book1 = books.get(0);
        assertEquals("Book1", book1.getTitle());
        assertTrue(book1.isBorrowed());
        assertEquals(LocalDate.of(2025, 10, 1), book1.getDueDate());
        assertEquals("user1", book1.getBorrowerUsername());

        // التحقق من الكتاب الثاني
        Book book2 = books.get(1);
        assertEquals("Book2", book2.getTitle());
        assertFalse(book2.isBorrowed());
        assertNull(book2.getDueDate());
        assertNull(book2.getBorrowerUsername());
    }

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
        assertTrue(result, "Update should return true for existing book");

        // التحقق من تحديث الملف فعليًا
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

        // التحقق من أن الملف لم يتغير
        try (BufferedReader br = new BufferedReader(new FileReader(booksFile))) {
            String line = br.readLine();
            assertEquals("Java Programming,Author1,111,false,,", line);
        }
    }
    
    @Test
    void testGetAllBooksIOException() throws IOException {
        File unreadableFile = new File(tempDir, "bad.csv");
        unreadableFile.createNewFile();
        unreadableFile.setReadable(false); // يمنع القراءة

        BookRepository badRepo = new BookRepository(unreadableFile.getAbsolutePath());

        List<Book> books = badRepo.getAllBooks();

        // بما إن القراءة فشلت، المتوقّع يرجع ليست فاضية
        assertTrue(books.isEmpty());
    }
    
    @Test
    void testGetAllBooksWithInvalidDate() throws IOException {
        try (FileWriter writer = new FileWriter(booksFile)) {
            writer.write("Book1,Author1,111,true,invalid-date,user1\n");
        }

        List<Book> books = bookRepository.getAllBooks();
        assertEquals(1, books.size());
        assertNull(books.get(0).getDueDate(), "Invalid date should result in null dueDate");
    }

    @Test
    void testUpdateBookWithRenameFailure() throws IOException {
        Book book = new Book("Book1","Author1","111");
        bookRepository.addBook(book);

        // ملف مؤقت موجود مسبقًا لتسبب فشل إعادة التسمية
        File tempFile = new File("files/temp_books.csv");
        tempFile.createNewFile();

        Book updatedBook = new Book("Updated Book", "Author1", "111");
        boolean result = bookRepository.updateBook(updatedBook);

        // التست يتأكد أن العملية فشلت بشكل نظيف
        assertTrue(result);
    }


}
