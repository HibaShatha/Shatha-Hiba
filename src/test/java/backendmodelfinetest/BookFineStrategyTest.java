package backendmodelfinetest;



import org.junit.jupiter.api.Test;

import backend.model.fine.BookFineStrategy;

import static org.junit.jupiter.api.Assertions.*;

class BookFineStrategyTest {

    private final BookFineStrategy strategy = new BookFineStrategy();

    @Test
    void testCalculateFine_OneDayOverdue_Returns10() {
        double fine = strategy.calculateFine(1);
        assertEquals(10.0, fine, "Fine should be 10 NIS for 1 day overdue");
    }

    @Test
    void testCalculateFine_MultipleDaysOverdue_Returns10() {
        double fine = strategy.calculateFine(5);
        assertEquals(10.0, fine, "Fine should be 10 NIS regardless of days");
    }

    @Test
    void testCalculateFine_ZeroDaysOverdue_Returns10() {
        double fine = strategy.calculateFine(0);
        assertEquals(10.0, fine, "Fine should be 10 NIS even if not overdue (edge case)");
    }

    @Test
    void testCalculateFine_NegativeDays_Returns10() {
        double fine = strategy.calculateFine(-3);
        assertEquals(10.0, fine, "Fine should be 10 NIS even for negative days (invalid input)");
    }

    @Test
    void testCalculateFine_LargeNumberOfDays_Returns10() {
        double fine = strategy.calculateFine(100);
        assertEquals(10.0, fine, "Fine should still be 10 NIS for extreme overdue");
    }
}