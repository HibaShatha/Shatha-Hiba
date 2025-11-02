package backend.model.fine;

/**
 * Strategy interface for calculating fines for overdue media items.
 * <p>
 * Different media types (e.g., Book, CD) can implement this interface
 * to provide their own fine calculation logic.
 * </p>
 * 
 * <p>
 * Encourages the use of the Strategy design pattern for flexible fine calculation.
 * </p>
 * 
 * @author Shatha_Dweikat
 * @version 1.0.0
 */
public interface FineStrategy {
    double calculateFine(int overdueDays);
}
