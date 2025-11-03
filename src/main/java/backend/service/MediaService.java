package backend.service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import backend.model.Book;
import backend.model.CD;
import backend.repository.BookRepository;
import backend.repository.CDRepository;
import backend.repository.FineRepository;
import backend.strategy.Search;
import backend.strategy.SearchStrategy;
/**
 * Service class for managing Books and CDs, including borrowing, returning,
 * fines, and administrative actions.
 * 
 * @author Hiba_ibraheem
 * @version 2.0 // updated after adding CD management and fine calculations
 */
public class MediaService {
    private BookRepository bookRepo = new BookRepository();
    private CDRepository cdRepo = new CDRepository();
    private FineRepository fineRepo = new FineRepository();
    private AdminService adminService;

    public MediaService(AdminService adminService) {
        this.adminService = adminService;
    }

    // =================== Books ===================
    public void addBookWithStartingIsbn(String title, String author, int startingIsbn, int quantity) {
        if (!adminService.isLoggedIn()) { System.out.println("Please login as admin first!"); return; }
        if (title == null || title.isEmpty() || author == null || author.isEmpty() || quantity <= 0) {
            System.out.println("Error: Title, author, and quantity must be provided!"); return;
        }

        List<String> existingIsbns = new ArrayList<>(bookRepo.getAllBooks().stream().map(Book::getIsbn).toList());
        int isbnNumber = startingIsbn;

        for (int i = 0; i < quantity; i++) {
            while (existingIsbns.contains(String.valueOf(isbnNumber))) isbnNumber++;
            String isbn = String.valueOf(isbnNumber);
            Book book = new Book(title, author, isbn);
            bookRepo.addBook(book);
            existingIsbns.add(isbn);
            isbnNumber++;
            System.out.println("ISBN " + isbn + " set as unique for the new copy!");
        }

        System.out.println("Books added successfully!");
    }
   
    public List<Book> searchBooks(Search searchContext, String keyword) {
        return searchContext.performSearch(getAllBooks(), keyword);
    }



    public List<Book> getAllBooks() {
        return bookRepo.getAllBooks();
    }

    public void borrowBook(String isbn, String username) {
        if (getUserFineBalance(username) > 0) { System.out.println("Cannot borrow: Outstanding fine exists."); return; }
        if (getOverdueBooks().stream().anyMatch(b -> username.equals(b.getBorrowerUsername()))) {
            System.out.println("Cannot borrow: You have overdue books."); return;
        }

        for (Book book : bookRepo.getAllBooks()) {
            if (book.getIsbn().equals(isbn)) {
                if (book.isBorrowed()) { System.out.println("Book is already borrowed by " + book.getBorrowerUsername()); return; }
                book.borrow(username);
                bookRepo.updateBook(book);
                System.out.println("Book borrowed successfully by " + username + ". Due: " + book.getDueDate());
                return;
            }
        }
        System.out.println("Book with ISBN " + isbn + " not found!");
    }

    public void returnBook(String isbn, String username) {
        for (Book book : bookRepo.getAllBooks()) {
            if (book.getIsbn().equals(isbn)) {
                if (!book.isBorrowed() || !username.equals(book.getBorrowerUsername())) {
                    System.out.println("You haven't borrowed this book!"); return;
                }
                double fine = book.calculateFine();
                if (fine > 0) {
                    double currentBalance = fineRepo.getFineBalance(username);
                    fineRepo.updateFineBalance(username, currentBalance + fine);
                    System.out.println("Overdue fine of $" + fine + " added.");
                }
                book.returned();
                bookRepo.updateBook(book);
                System.out.println("Book returned successfully!");
                return;
            }
        }
        System.out.println("Book with ISBN " + isbn + " not found!");
    }

    public List<Book> getOverdueBooks() {
        return bookRepo.getAllBooks().stream().filter(Book::isOverdue).collect(Collectors.toList());
    }

    public double getUserFineBalance(String username) {
        double balance = fineRepo.getFineBalance(username);
        double pendingBookFines = getOverdueBooks().stream()
                                    .filter(b -> username.equals(b.getBorrowerUsername()))
                                    .mapToDouble(Book::calculateFine)
                                    .sum();
        double pendingCDFines = getOverdueCDs().stream()
                                    .filter(cd -> username.equals(cd.getBorrowerUsername()))
                                    .mapToDouble(CD::calculateFine)
                                    .sum();
        return balance + pendingBookFines + pendingCDFines;
    }


    public void payFine(String username, double amount) {
        double totalBalance = getUserFineBalance(username);
        if (totalBalance <= 0) { System.out.println("No fine to pay!"); return; }
        if (amount <= 0) { System.out.println("Payment amount must be positive!"); return; }

        if (amount >= totalBalance) {
            fineRepo.updateFineBalance(username, 0.0);
            System.out.println("Paid full balance: $" + totalBalance);
        } else {
            double currentBalance = fineRepo.getFineBalance(username);
            fineRepo.updateFineBalance(username, currentBalance - amount);
            System.out.println("Paid $" + amount + ". Remaining: $" + getUserFineBalance(username));
        }
    }
 // =================== Get Repositories for testing ===================
    public BookRepository getBookRepo() {
        return this.bookRepo;
    }

    public CDRepository getCDRepo() {
        return this.cdRepo;
    }

    public FineRepository getFineRepo() {
        return this.fineRepo;
    }

    // =================== CDs ===================
    public void addCD(String title, String artist) {
        if (!adminService.isLoggedIn()) { System.out.println("Please login as admin first!"); return; }
        CD cd = new CD(title, artist);
        cdRepo.addCD(cd);
        System.out.println("CD added successfully!");
    }

    public List<CD> searchCD(String keyword) {
        return cdRepo.getAllCDs().stream()
                    .filter(cd -> cd.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                  cd.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
    }

    public List<CD> getAllCDs() {
        return cdRepo.getAllCDs();
    }

    public void borrowCD(String title, String username) {
        if (getUserFineBalance(username) > 0) { System.out.println("Cannot borrow: Outstanding fine exists."); return; }

        for (CD cd : cdRepo.getAllCDs()) {
            if (cd.getTitle().equalsIgnoreCase(title)) {
                if (cd.isBorrowed()) { System.out.println("CD already borrowed by " + cd.getBorrowerUsername()); return; }
                cd.borrow(username);
                cdRepo.updateCD(cd);
                System.out.println("CD borrowed successfully by " + username + ". Due: " + cd.getDueDate());
                return;
            }
        }
        System.out.println("CD not found!");
    }

    public void returnCD(String title, String username) {
        for (CD cd : cdRepo.getAllCDs()) {
            if (cd.getTitle().equalsIgnoreCase(title)) {
                if (!cd.isBorrowed() || !username.equals(cd.getBorrowerUsername())) {
                    System.out.println("You haven't borrowed this CD!"); return;
                }
                double fine = cd.calculateFine();
                if (fine > 0) {
                    double currentBalance = fineRepo.getFineBalance(username);
                    fineRepo.updateFineBalance(username, currentBalance + fine);
                    System.out.println("Overdue fine of $" + fine + " added.");
                }
                cd.returned();
                cdRepo.updateCD(cd);
                System.out.println("CD returned successfully!");
                return;
            }
        }
        System.out.println("CD not found!");
    }

    public List<CD> getOverdueCDs() {
        return cdRepo.getAllCDs().stream().filter(CD::isOverdue).collect(Collectors.toList());
    }
}