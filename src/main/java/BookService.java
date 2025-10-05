import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    private BookRepository bookRepo = new BookRepository();
    private AdminService adminService;
    private FineRepository fineRepo = new FineRepository();

    public BookService(AdminService adminService) {
        this.adminService = adminService;
    }

    public void addBook(Book book) {
        if (!adminService.isLoggedIn()) {
            System.out.println("Please login as admin first!");
            return;
        }
        bookRepo.addBook(book);
        System.out.println("Book added successfully: " + book.getTitle());
    }

    public List<Book> searchBook(String keyword) {
        return bookRepo.search(keyword);
    }

    public List<Book> getAllBooks() {
        return bookRepo.getAllBooks();
    }



 // User borrow
    public void borrowBook(String isbn, String username) {
        double fineBalance = fineRepo.getFineBalance(username);
        if (fineBalance > 0) {
            System.out.println("Cannot borrow: You have an outstanding fine of $" + fineBalance + ". Please pay all fines first.");
            return;
        }

        List<Book> books = bookRepo.getAllBooks();
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                if (book.isBorrowed()) {
                    System.out.println("Book is already borrowed by " + book.getBorrowerUsername() + "! Due date: " + book.getDueDate());
                    return;
                }
                book.borrow(username); // sets dueDate = today + 28 days
                if (bookRepo.updateBook(book)) {
                    System.out.println("Book borrowed successfully by " + username + "! Due date: " + book.getDueDate());
                } else {
                    System.out.println("Error borrowing book!");
                }
                return;
            }
        }
        System.out.println("Book with ISBN " + isbn + " not found!");
    }


    // User return
    public void returnBook(String isbn, String username) {
        List<Book> books = bookRepo.getAllBooks();
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                if (!book.isBorrowed() || !username.equals(book.getBorrowerUsername())) {
                    System.out.println("You haven't borrowed this book or it's not borrowed!");
                    return;
                }
                double fine = book.calculateFine();
                if (fine > 0) {
                    double currentBalance = fineRepo.getFineBalance(username);
                    fineRepo.updateFineBalance(username, currentBalance + fine);
                    System.out.println("Overdue fine of $" + fine + " added to your account.");
                }
                book.returned();
                if (bookRepo.updateBook(book)) {
                    System.out.println("Book returned successfully!");
                } else {
                    System.out.println("Error returning book!");
                }
                return;
            }
        }
        System.out.println("Book with ISBN " + isbn + " not found!");
    }


    public List<Book> getOverdueBooks() {
        return bookRepo.getAllBooks().stream()
                      .filter(Book::isOverdue)
                      .collect(Collectors.toList());
    }

   
    public double getUserFineBalance(String username) {
        double balance = fineRepo.getFineBalance(username);
        double pendingFines = getOverdueBooks().stream()
                                               .filter(b -> b.getBorrowerUsername() != null && b.getBorrowerUsername().equals(username))
                                               .mapToDouble(Book::calculateFine)
                                               .sum();
        return balance + pendingFines;
    }

    
    public void payFine(String username, double amount) {
        double currentBalance = fineRepo.getFineBalance(username);
        if (amount <= 0) {
            System.out.println("Payment amount must be positive!");
            return;
        }
        if (amount > currentBalance) {
            System.out.println("Payment of $" + amount + " exceeds fine balance of $" + currentBalance + ". Paying full balance.");
            amount = currentBalance;
        }
        fineRepo.updateFineBalance(username, currentBalance - amount);
        System.out.println("Paid $" + amount + ". Remaining fine balance: $" + fineRepo.getFineBalance(username));
    }
}