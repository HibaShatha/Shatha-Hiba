package backend.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import backend.model.fine.BookFineStrategy;

/**
 * Represents a Book entity that extends the {@link Media} class.
 * <p>
 * A Book can be borrowed and returned by a user, and a fine can be calculated
 * if it is returned after its due date. The fine calculation is delegated to
 * a {@link BookFineStrategy}.
 * </p>
 *
 * <p>
 * Default borrowing period is 28 days.
 * </p>
 *
 * @author Hiba_ibraheem
 * @version 1.0
 */
public class Book extends Media {

    /**
     * Constructs a new Book object with the specified title, author, and ISBN.
     * Automatically assigns a {@link BookFineStrategy} for fine calculation.
     *
     * @param title  the title of the book
     * @param author the author of the book
     * @param isbn   the ISBN identifier for the book
     */
    public Book(String title, String author, String isbn) {
        super(title, author, isbn, new BookFineStrategy());
    }

    /**
     * Marks the book as borrowed by a specific user.
     * <p>
     * The due date is set to 28 days from the current date.
     * </p>
     *
     * @param username the username of the borrower
     */
    @Override
    public void borrow(String username) {
        this.borrowed = true;
        this.borrowerUsername = username;
        this.dueDate = LocalDate.now().plusDays(28);
    }

    /**
     * Marks the book as returned.
     * <p>
     * Resets borrowing status, borrower username, and due date.
     * </p>
     */
    @Override
    public void returned() {
        this.borrowed = false;
        this.borrowerUsername = null;
        this.dueDate = null;
    }

    /**
     * Checks whether the book is overdue.
     *
     * @return {@code true} if the book is borrowed and the current date
     *         is after the due date; {@code false} otherwise
     */
    @Override
    public boolean isOverdue() {
        return borrowed && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Calculates the fine for the book if it is overdue.
     * <p>
     * The fine amount is determined based on the number of overdue days
     * using the {@link BookFineStrategy}.
     * </p>
     *
     * @return the fine amount; returns {@code 0.0} if the book is not overdue
     */
    @Override
    public double calculateFine() {
        if (!isOverdue()) return 0.0;
        long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return fineStrategy.calculateFine((int) overdueDays);
    }
}
