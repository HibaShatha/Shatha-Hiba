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
        File bookFile = new File("books.csv");
        if (bookFile.exists()) bookFile.delete();
        bookFile.createNewFile();

        File cdFile = new File("cds.csv");
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
    void testFinesWorkflow1() {
        // أضف كتاب واجعله متأخر
        mediaService.addBookWithStartingIsbn("Late Book", "Author", 3000, 1);
        Book book = mediaService.getAllBooks().get(0);
        book.borrow("user1");
        book.setDueDate(LocalDate.now().minusDays(4)); // overdue
        mediaService.getBookRepo().updateBook(book);

        // تحقق من الغرامة الأولية
        double balance = mediaService.getUserFineBalance("user1");
        assertTrue(balance > 0, "Expected a positive fine balance for overdue book");

        // حاول دفع مبلغ أقل من الغرامة
        outputStreamCaptor.reset();
        double partialPayment = balance / 2;
        mediaService.payFine("user1", partialPayment);
        String partialOutput = outputStreamCaptor.toString();
        assertTrue(partialOutput.contains("Paid $" + partialPayment));
        double remainingBalance = mediaService.getUserFineBalance("user1");
        assertEquals(balance - partialPayment, remainingBalance, 0.001);

        // دفع كامل الباقي
        outputStreamCaptor.reset();
        mediaService.payFine("user1", remainingBalance);
        String fullOutput = outputStreamCaptor.toString();
        assertTrue(fullOutput.contains("Paid full balance"));
        assertNotEquals(0, mediaService.getUserFineBalance("user1"), 0.001);
    }


    @Test
    void testAddCD() {
        outputStreamCaptor.reset();
        mediaService.addCD("Hybrid Theory", "Linkin Park");
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("CD added successfully!"));
    }

  

    @Test
    void testSearchCD() {
        mediaService.addCD("Hybrid Theory", "Linkin Park");
        assertEquals(1, mediaService.searchCD("Hybrid").size());
        assertEquals(0, mediaService.searchCD("NonExisting").size());
    }

    @Test
    void testBorrowAndReturnBook() {
        mediaService.addBookWithStartingIsbn("Clean Code", "Robert C. Martin", 2000, 1);
        outputStreamCaptor.reset();

        mediaService.borrowBook("2000", "user1");
        String borrowOutput = outputStreamCaptor.toString();
        assertTrue(borrowOutput.contains("Book borrowed successfully by user1"));

        outputStreamCaptor.reset();
        mediaService.returnBook("2000", "user1");
        String returnOutput = outputStreamCaptor.toString();
        assertTrue(returnOutput.contains("Book returned successfully!"));
    }

    @Test
    void testBorrowAndReturnCD() {
        mediaService.addCD("Hybrid Theory", "Linkin Park");
        outputStreamCaptor.reset();

        mediaService.borrowCD("Hybrid Theory", "user1");
        String borrowOutput = outputStreamCaptor.toString();
        assertTrue(borrowOutput.contains("CD borrowed successfully by user1"));

        outputStreamCaptor.reset();
        mediaService.returnCD("Hybrid Theory", "user1");
        String returnOutput = outputStreamCaptor.toString();
        assertTrue(returnOutput.contains("CD returned successfully!"));
    }

    @Test
    void testOverdueBooksAndCDs() {
        // أضف الكتاب عن طريق MediaService
        mediaService.addBookWithStartingIsbn("Old Book", "Author", 1001, 1);
        Book book = mediaService.getAllBooks().get(0);
        book.borrow("user1");
        book.setDueDate(LocalDate.now().minusDays(5)); // جعله overdue
        // حدث الـ repository
        mediaService.getBookRepo().updateBook(book);

        assertEquals(1, mediaService.getOverdueBooks().size());

        // أضف CD
        mediaService.addCD("Old CD", "Artist");
        CD cd = mediaService.getAllCDs().get(0);
        cd.borrow("user1");
        cd.setDueDate(LocalDate.now().minusDays(3)); // جعله overdue
        // حدث الـ repository
        mediaService.getCDRepo().updateCD(cd);

        assertEquals(1, mediaService.getOverdueCDs().size());
    }

    @Test
    void testPayFine() {
        // مبدئيًا لا fines
        outputStreamCaptor.reset();
        mediaService.payFine("user1", 10);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("No fine to pay!"));
    }
    
    @Test
    void testFinesWorkflow() {
        // أضف كتاب واجعله متأخر
        mediaService.addBookWithStartingIsbn("Late Book", "Author", 3000, 1);
        Book book = mediaService.getAllBooks().get(0);
        book.borrow("user1");
        book.setDueDate(LocalDate.now().minusDays(4)); // overdue
        mediaService.getBookRepo().updateBook(book);

        // تحقق من الغرامة الأولية
        double balance = mediaService.getUserFineBalance("user1");
        assertTrue(balance > 0, "Expected a positive fine balance for overdue book");

        // دفع جزء من الغرامة
        outputStreamCaptor.reset();
        double partialPayment = balance / 2;
        mediaService.payFine("user1", partialPayment);
        String partialOutput = outputStreamCaptor.toString();
        assertTrue(partialOutput.contains("Paid $" + partialPayment));
        double remainingBalance = mediaService.getUserFineBalance("user1");
        assertEquals(balance - partialPayment, remainingBalance, 0.001);

        // دفع الباقي
        outputStreamCaptor.reset();
        mediaService.payFine("user1", remainingBalance);
        String fullOutput = outputStreamCaptor.toString();
        assertTrue(fullOutput.contains("Paid full balance"));

        // رجع الكتاب عشان ما يكون فيه fine إضافي
        book.returned();
        mediaService.getBookRepo().updateBook(book);

        // الآن الرصيد لازم يكون صفر
        assertEquals(0, mediaService.getUserFineBalance("user1"), 0.001);
    }
    
    @Test
    void testOverdueFineTriggeredIndependently() {
        // ======= إعداد الكتاب المتأخر =======
        mediaService.addBookWithStartingIsbn("Independent Overdue Book", "Author", 6000, 1);
        Book book = mediaService.getAllBooks().get(0);
        book.borrow("user_independent");
        // ضبط التاريخ ليكون متأخر → يخلق fine > 0
        book.setDueDate(LocalDate.now().minusDays(5));
        mediaService.getBookRepo().updateBook(book);

        // ======= ارجاع الكتاب =======
        outputStreamCaptor.reset();
        mediaService.returnBook("6000", "user_independent");
        String outputBook = outputStreamCaptor.toString();

        // تحقق من وصولنا للـ if (fine > 0)
        assertTrue(outputBook.contains("Overdue fine of $"), "Expected overdue fine to be added");
        assertTrue(outputBook.contains("Book returned successfully!"));

        // تحقق أن الغرامة فعليًا اتحدثت في FineRepository
        double balance = mediaService.getFineRepo().getFineBalance("user_independent");
        assertTrue(balance > 0, "Expected fine balance > 0 for overdue book");

        // ======= إعداد CD متأخر =======
        mediaService.addCD("Independent Overdue CD", "Artist");
        CD cd = mediaService.getAllCDs().get(0);
        cd.borrow("user_independent");
        cd.setDueDate(LocalDate.now().minusDays(3));
        mediaService.getCDRepo().updateCD(cd);

        // ======= ارجاع CD =======
        outputStreamCaptor.reset();
        mediaService.returnCD("Independent Overdue CD", "user_independent");
        String outputCD = outputStreamCaptor.toString();

        // تحقق من الغرامة على CD
        assertTrue(outputCD.contains("Overdue fine of $"), "Expected overdue fine to be added for CD");
        assertTrue(outputCD.contains("CD returned successfully!"));

        // تحقق من الرصيد بعد CD
        double totalBalance = mediaService.getUserFineBalance("user_independent");
        assertTrue(totalBalance > 0, "Expected fine balance > 0 including overdue CD");
    }


    
}
