package backend.model;

import java.time.LocalDate;
import backend.model.fine.FineStrategy;
/**
 * Abstract class representing a general media item in the system.
 * <p>
 * This can be a Book, CD, or any other media type. It stores basic
 * information like title, author, ISBN (if applicable), borrowing status,
 * due date, and the borrower’s username.
 * </p>
 * 
 * <p>
 * Each subclass must implement borrowing logic, returning, overdue check,
 * and fine calculation according to its specific rules.
 * </p>
 *
 * @author Shatha_Dweikat
 * @version 1.0.0
 */
public abstract class Media {
    protected String title;
    protected String author;
    protected String isbn;
    public boolean borrowed;
    public LocalDate dueDate;
    public String borrowerUsername;
    protected FineStrategy fineStrategy;

  
    public Media(String title, String author, String isbn, FineStrategy fineStrategy) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = false;
        this.dueDate = null;
        this.borrowerUsername = null;
        this.fineStrategy = fineStrategy;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isBorrowed() { return borrowed; }
    public LocalDate getDueDate() { return dueDate; }
    public String getBorrowerUsername() { return borrowerUsername; }

    public abstract void borrow(String username);
    public abstract void returned();
    public abstract boolean isOverdue();
    public abstract double calculateFine();
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getMediaType() {
        return this.getClass().getSimpleName();
    }
}
