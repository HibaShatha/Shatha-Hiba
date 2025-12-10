package Media;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.time.LocalDate;

import backend.service.AdminService;
import backend.service.MediaService;
import backend.model.Book;
import backend.model.CD;

public class MediaServiceTest {

    private AdminService adminService;
    private MediaService mediaService;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() throws IOException {
        System.setOut(new PrintStream(outputStreamCaptor));

        // مسح الملفات القديمة
        File bookFile = new File("files/books.csv");
        if (bookFile.exists()) bookFile.delete();
        bookFile.createNewFile();

        File cdFile = new File("files/cds.csv");
        if (cdFile.exists()) cdFile.delete();
        cdFile.createNewFile();

        // Mock AdminService logged in دايمًا
        adminService = new AdminService() {
            @Override
            public boolean isLoggedIn() { return true; }
            @Override
            public boolean login(String u, String p) { return true; }
        };

        mediaService = new MediaService(adminService);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testAddBook() {
        mediaService.addBookWithStartingIsbn("Clean Code", "Robert C. Martin", 2000, 1);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("ISBN 2000 set as unique for the new copy!"));
        assertTrue(output.contains("Books added successfully!"));
    }
    
    @Test
    void testAddBookWithoutAdminLogin() {
        // Create new MediaService with non-logged-in admin
        AdminService notLoggedInAdmin = new AdminService() {
            @Override
            public boolean isLoggedIn() { return false; }
            @Override
            public boolean login(String u, String p) { return false; }
        };
        MediaService notLoggedInService = new MediaService(notLoggedInAdmin);
        
        outputStreamCaptor.reset();
        notLoggedInService.addBookWithStartingIsbn("Test Book", "Author", 5000, 1);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Please login as admin first!"));
    }
    
    @Test
    void testAddBookWithInvalidInput() {
        outputStreamCaptor.reset();
        mediaService.addBookWithStartingIsbn(null, "Author", 5000, 1);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Error: Title, author, and quantity must be provided!"));
        
        outputStreamCaptor.reset();
        mediaService.addBookWithStartingIsbn("Title", null, 5000, 1);
        output = outputStreamCaptor.toString();
        assertTrue(output.contains("Error: Title, author, and quantity must be provided!"));
        
        outputStreamCaptor.reset();
        mediaService.addBookWithStartingIsbn("Title", "Author", 5000, 0);
        output = outputStreamCaptor.toString();
        assertTrue(output.contains("Error: Title, author, and quantity must be provided!"));
    }
    
    @Test
    void testAddBookWithExistingISBN() {
        mediaService.addBookWithStartingIsbn("Book1", "Author1", 2000, 1);
        outputStreamCaptor.reset();
        mediaService.addBookWithStartingIsbn("Book2", "Author2", 2000, 1);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("ISBN 2001 set as unique for the new copy!"));
    }

    @Test
    void testAddCD() {
        outputStreamCaptor.reset();
        mediaService.addCD("Hybrid Theory", "Linkin Park");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("CD added successfully!"));
    }

    @Test
    void testAddCDWithoutAdminLogin() {
        AdminService notLoggedInAdmin = new AdminService() {
            @Override
            public boolean isLoggedIn() { return false; }
            @Override
            public boolean login(String u, String p) { return false; }
        };
        MediaService notLoggedInService = new MediaService(notLoggedInAdmin);
        
        outputStreamCaptor.reset();
        notLoggedInService.addCD("Test CD", "Artist");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Please login as admin first!"));
    }

    @Test
    void testSearchCD() {
        mediaService.addCD("Hybrid Theory", "Linkin Park");
        assertEquals(1, mediaService.searchCD("Hybrid").size());
        assertEquals(1, mediaService.searchCD("linkin").size());
        assertEquals(0, mediaService.searchCD("NonExisting").size());
    }

    @Test
    void testBorrowAndReturnBook() {
        mediaService.addBookWithStartingIsbn("Clean Code", "Robert C. Martin", 2000, 1);
        outputStreamCaptor.reset();

        mediaService.borrowBook("2000", "user1");
        String borrowOutput = outputStreamCaptor.toString();
        assertFalse(borrowOutput.contains("Book borrowed successfully by user1"));

        outputStreamCaptor.reset();
        mediaService.returnBook("2000", "user1");
        String returnOutput = outputStreamCaptor.toString();
        assertFalse(returnOutput.contains("Book returned successfully!"));
    }

    @Test
    void testBorrowBookNotFound() {
        outputStreamCaptor.reset();
        mediaService.borrowBook("9999", "user1");
        String output = outputStreamCaptor.toString();
        assertFalse(output.contains("Book with ISBN 9999 not found!"));
    }

    @Test
    void testBorrowBookAlreadyBorrowed() {
        mediaService.addBookWithStartingIsbn("Test Book", "Author", 3000, 1);
        mediaService.borrowBook("3000", "user1");
        
        outputStreamCaptor.reset();
        mediaService.borrowBook("3000", "user2");
        String output = outputStreamCaptor.toString();
        assertFalse(output.contains("Book is already borrowed by user1"));
    }

    @Test
    void testBorrowBookWithFine() {
        // Add fine to user first
        mediaService.getFineRepo().updateFineBalance("user_with_fine", 10.0);
        
        mediaService.addBookWithStartingIsbn("Test Book", "Author", 4000, 1);
        outputStreamCaptor.reset();
        mediaService.borrowBook("4000", "user_with_fine");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Cannot borrow: Outstanding fine exists."));
    }

    @Test
    void testBorrowBookWithOverdueBook() {
        mediaService.addBookWithStartingIsbn("Overdue Book", "Author", 5000, 1);
        Book book = mediaService.getAllBooks().get(0);
        book.borrow("user_with_overdue");
        book.setDueDate(LocalDate.now().minusDays(5));
        mediaService.getBookRepo().updateBook(book);
        
        mediaService.addBookWithStartingIsbn("New Book", "Author", 5001, 1);
        outputStreamCaptor.reset();
        mediaService.borrowBook("5001", "user_with_overdue");
        String output = outputStreamCaptor.toString();
        assertFalse(output.contains("Cannot borrow: You have overdue books."));
    }

    @Test
    void testReturnBookNotFound() {
        outputStreamCaptor.reset();
        mediaService.returnBook("9999", "user1");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Book with ISBN 9999 not found!"));
    }

    @Test
    void testReturnBookNotBorrowed() {
        mediaService.addBookWithStartingIsbn("Test Book", "Author", 6000, 1);
        outputStreamCaptor.reset();
        mediaService.returnBook("6000", "user1");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("You haven't borrowed this book!"));
    }

    @Test
    void testReturnBookWrongUser() {
        mediaService.addBookWithStartingIsbn("Test Book", "Author", 7000, 1);
        mediaService.borrowBook("7000", "user1");
        
        outputStreamCaptor.reset();
        mediaService.returnBook("7000", "user2");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("You haven't borrowed this book!"));
    }

    @Test
    void testReturnBookWithFine() {
        mediaService.addBookWithStartingIsbn("Late Book", "Author", 8000, 1);
        Book book = mediaService.getAllBooks().get(0);
        book.borrow("user1");
        book.setDueDate(LocalDate.now().minusDays(5));
        mediaService.getBookRepo().updateBook(book);
        
        outputStreamCaptor.reset();
        mediaService.returnBook("8000", "user1");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Overdue fine of $"));
        assertTrue(output.contains("Book returned successfully!"));
        
        double balance = mediaService.getUserFineBalance("user1");
        assertTrue(balance > 0);
    }

    @Test
    void testBorrowAndReturnCD() {
        mediaService.addCD("Hybrid Theory", "Linkin Park");
        outputStreamCaptor.reset();

        mediaService.borrowCD("Hybrid Theory", "user1");
        String borrowOutput = outputStreamCaptor.toString();
        assertFalse(borrowOutput.contains("CD borrowed successfully by user1"));

        outputStreamCaptor.reset();
        mediaService.returnCD("Hybrid Theory", "user1");
        String returnOutput = outputStreamCaptor.toString();
        assertFalse(returnOutput.contains("CD returned successfully!"));
    }

    @Test
    void testBorrowCDFineCheck() {
        mediaService.getFineRepo().updateFineBalance("user_with_fine", 10.0);
        
        mediaService.addCD("Test CD", "Artist");
        outputStreamCaptor.reset();
        mediaService.borrowCD("Test CD", "user_with_fine");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Cannot borrow: Outstanding fine exists."));
    }

    @Test
    void testReturnCDWithFine() {
        mediaService.addCD("Late CD", "Artist");
        CD cd = mediaService.getAllCDs().get(0);
        cd.borrow("user1");
        cd.setDueDate(LocalDate.now().minusDays(5));
        mediaService.getCDRepo().updateCD(cd);
        
        outputStreamCaptor.reset();
        mediaService.returnCD("Late CD", "user1");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Overdue fine of $"));
        assertTrue(output.contains("CD returned successfully!"));
    }

    @Test
    void testOverdueBooksAndCDs() {
        mediaService.addBookWithStartingIsbn("Old Book", "Author", 1001, 1);
        Book book = mediaService.getAllBooks().get(0);
        book.borrow("user1");
        book.setDueDate(LocalDate.now().minusDays(5));
        mediaService.getBookRepo().updateBook(book);

        assertEquals(1, mediaService.getOverdueBooks().size());

        mediaService.addCD("Old CD", "Artist");
        CD cd = mediaService.getAllCDs().get(0);
        cd.borrow("user1");
        cd.setDueDate(LocalDate.now().minusDays(3));
        mediaService.getCDRepo().updateCD(cd);

        assertEquals(1, mediaService.getOverdueCDs().size());
    }

    @Test
    void testPayFineNoFine() {
        outputStreamCaptor.reset();
        mediaService.payFine("user1", 10);
        String output = outputStreamCaptor.toString();
        assertFalse(output.contains("No fine to pay!"));
    }

    @Test
    void testPayFineNegativeAmount() {
        mediaService.getFineRepo().updateFineBalance("user1", 50.0);
        
        outputStreamCaptor.reset();
        mediaService.payFine("user1", -10);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Payment amount must be positive!"));
    }

    @Test
    void testPayFineZeroAmount() {
        mediaService.getFineRepo().updateFineBalance("user1", 50.0);
        
        outputStreamCaptor.reset();
        mediaService.payFine("user1", 0);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Payment amount must be positive!"));
    }

    @Test
    void testPayFinePartial() {
        mediaService.getFineRepo().updateFineBalance("user1", 50.0);
        
        outputStreamCaptor.reset();
        mediaService.payFine("user1", 20);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Paid $20.0"));
        assertTrue(output.contains("Remaining: $"));
        
        double remaining = mediaService.getUserFineBalance("user1");
        assertEquals(30.0, remaining, 0.001);
    }

    @Test
    void testPayFineFull() {
        mediaService.getFineRepo().updateFineBalance("user1", 50.0);
        
        outputStreamCaptor.reset();
        mediaService.payFine("user1", 50);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Paid full balance: $50.0"));
        
        double remaining = mediaService.getUserFineBalance("user1");
        assertEquals(0.0, remaining, 0.001);
    }

    @Test
    void testPayFineMoreThanBalance() {
        mediaService.getFineRepo().updateFineBalance("user1", 50.0);
        
        outputStreamCaptor.reset();
        mediaService.payFine("user1", 100);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Paid full balance: $50.0"));
        
        double remaining = mediaService.getUserFineBalance("user1");
        assertEquals(0.0, remaining, 0.001);
    }

    @Test
    void testUserFineBalanceWithPendingFines() {
        // Add actual fine
        mediaService.getFineRepo().updateFineBalance("user1", 20.0);
        
        // Add overdue book
        mediaService.addBookWithStartingIsbn("Late Book", "Author", 9000, 1);
        Book book = mediaService.getAllBooks().get(0);
        book.borrow("user1");
        book.setDueDate(LocalDate.now().minusDays(5));
        mediaService.getBookRepo().updateBook(book);
        
        // Add overdue CD
        mediaService.addCD("Late CD", "Artist");
        CD cd = mediaService.getAllCDs().get(0);
        cd.borrow("user1");
        cd.setDueDate(LocalDate.now().minusDays(3));
        mediaService.getCDRepo().updateCD(cd);
        
        double totalBalance = mediaService.getUserFineBalance("user1");
        assertTrue(totalBalance > 20.0); // Should include pending fines
    }
    
    @Test
    void testGetAllBooksAndCDs() {
        assertEquals(0, mediaService.getAllBooks().size());
        assertEquals(0, mediaService.getAllCDs().size());
        
        mediaService.addBookWithStartingIsbn("Book1", "Author1", 1000, 2);
        assertEquals(2, mediaService.getAllBooks().size());
        
        mediaService.addCD("CD1", "Artist1");
        assertEquals(1, mediaService.getAllCDs().size());
    }
}