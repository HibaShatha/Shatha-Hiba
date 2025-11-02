package backend.model.fine;

/**
 * Fine calculation strategy for Books.
 * <p>
 * Implements the {@link FineStrategy} interface.
 * Books have a fixed fine per overdue day.
 * </p>
 * 
 * <p>
 * Example usage:
 * <pre>
 * FineStrategy bookFine = new BookFineStrategy();
 * double fine = bookFine.calculateFine(3); // returns 10
 * </pre>
 * </p>
 * 
 * <p>
 * Note: Here the fine is constant 10 NIS regardless of days overdue.
 * You can modify the logic to multiply by overdueDays if needed.
 * </p>
 * 
 * @author Shatha_Dweikat
 * @version 1.0.0
 */
public class BookFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for a Book.
     * <p>
     * Currently returns a fixed fine of 10 NIS regardless of overdue days.
     * </p>
     *
     * @param overdueDays the number of days the book is overdue (ignored in this strategy)
     * @return the fixed fine amount (10)
     */
    @Override
    public double calculateFine(int overdueDays) {
        return 10; // 10 NIS fixed fine per book
    }
}
