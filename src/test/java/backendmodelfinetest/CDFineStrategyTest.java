package backendmodelfinetest;

import org.junit.jupiter.api.Test;

import backend.model.fine.CDFineStrategy;

import static org.junit.jupiter.api.Assertions.*;

class CDFineStrategyTest {

    private final CDFineStrategy strategy = new CDFineStrategy();

    @Test
    void testCalculateFine_OneDayOverdue_Returns20() {
        double fine = strategy.calculateFine(1);
        assertEquals(20.0, fine, "Fine should be 20 NIS for 1 day overdue");
    }

    @Test
    void testCalculateFine_MultipleDaysOverdue_Returns20() {
        double fine = strategy.calculateFine(7);
        assertEquals(20.0, fine, "Fine should be 20 NIS regardless of days");
    }

    @Test
    void testCalculateFine_ZeroDaysOverdue_Returns20() {
        double fine = strategy.calculateFine(0);
        assertEquals(20.0, fine, "Fine should be 20 NIS even if not overdue");
    }

    @Test
    void testCalculateFine_NegativeDays_Returns20() {
        double fine = strategy.calculateFine(-5);
        assertEquals(20.0, fine, "Fine should be 20 NIS even for negative days (invalid input)");
    }

    @Test
    void testCalculateFine_LargeNumberOfDays_Returns20() {
        double fine = strategy.calculateFine(365);
        assertEquals(20.0, fine, "Fine should still be 20 NIS for extreme overdue");
    }
}