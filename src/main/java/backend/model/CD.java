package backend.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import backend.model.fine.CDFineStrategy;

/**
 * Represents a CD entity that extends the {@link Media} class.
 * <p>
 * A CD can be borrowed and returned by users. If it’s returned late,
 * a fine is calculated using the {@link CDFineStrategy}.
 * </p>
 *
 * <p>
 * Default borrowing period for CDs is <b>7 days</b>.
 * </p>
 *
 * @author Hiba_ibraheem
 * @version 1.0
 */
public class CD extends Media {

    /**
     * Constructs a new CD object with the specified title and author.
     * <p>
     * CDs do not have ISBNs, so the value is passed as {@code null}.
     * </p>
     *
     * @param title  the title of the CD
     * @param author the author or creator of the CD
     */
    public CD(String title, String author) { 
        super(title, author, null, new CDFineStrategy()); 
    }

    /**
     * Marks the CD as borrowed by a user.
     * <p>
     * The due date is set to 7 days from the borrowing date.
     * </p>
     *
     * @param username the username of the borrower
     */
    @Override
    public void borrow(String username) {
        this.borrowed = true;
        this.borrowerUsername = username;
        this.dueDate = LocalDate.now().plusDays(7);
    }

    /**
     * Marks the CD as returned and resets its borrowing information.
     */
    @Override
    public void returned() {
        this.borrowed = false;
        this.borrowerUsername = null;
        this.dueDate = null;
    }

    /**
     * Checks whether the CD is overdue.
     *
     * @return {@code true} if the CD is borrowed and the current date
     *         is after the due date; {@code false} otherwise
     */
    @Override
    public boolean isOverdue() {
        if (!isBorrowed()) return false;
        if (dueDate == null) return false;
        return LocalDate.now().isAfter(dueDate);
    }



    /**
     * Calculates the fine for the CD if it is overdue.
     * <p>
     * Uses the {@link CDFineStrategy} to determine the fine based on the
     * number of overdue days.
     * </p>
     *
     * @return the fine amount; returns {@code 0.0} if the CD is not overdue
     */
    @Override
    public double calculateFine() { 
    	if (!isOverdue()) 
    		return 0.0;
    	long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now()); 
    	return fineStrategy.calculateFine((int) overdueDays); 
    	}
}
