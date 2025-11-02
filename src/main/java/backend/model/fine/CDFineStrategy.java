package backend.model.fine;

/**
 * Fine calculation strategy for CDs.
 * <p>
 * Implements the {@link FineStrategy} interface.
 * CDs have a fixed fine regardless of the number of overdue days.
 * </p>
 * 
 * <p>
 * Borrowing period for CDs is typically shorter, but fine is constant.
 * </p>
 * 
 * <p>
 * Example usage:
 * <pre>
 * FineStrategy cdFine = new CDFineStrategy();
 * double fine = cdFine.calculateFine(5); // returns 20
 * </pre>
 * </p>
 * 
 * @author Hiba_ibraheem
 * @version 1.0.0
 */
public class CDFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for a CD.
     * <p>
     * CDs have a fixed fine of 20 regardless of overdue days.
     * </p>
     *
     * @param overdueDays the number of days the CD is overdue (ignored in this strategy)
     * @return the fixed fine amount (20)
     */
    @Override
    public double calculateFine(int overdueDays) {
        return 20;   
    }
}
