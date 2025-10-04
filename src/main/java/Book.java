import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Book {
    private String title;
    private String author;
    private String isbn;
    public boolean borrowed;
    private LocalDate dueDate;
    private String borrowerUsername;

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = false;
        this.dueDate = null;
        this.borrowerUsername = null;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isBorrowed() { return borrowed; }
    public LocalDate getDueDate() { return dueDate; }
    public String getBorrowerUsername() { return borrowerUsername; }

    public void borrow(String username) {
        this.borrowed = true;
        this.dueDate = LocalDate.now().plusDays(28); // Set due date to today + 28 days
        this.borrowerUsername = username;
    }

    public void returned() {
        this.borrowed = false;
        this.dueDate = null;
        this.borrowerUsername = null;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isOverdue() {
        if (!borrowed || dueDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    // New method to calculate fine for an overdue book ($1 per day overdue)
    public double calculateFine() {
        if (!isOverdue()) {
            return 0.0;
        }
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return daysOverdue * 1.0; // $1 per day overdue
    }
}