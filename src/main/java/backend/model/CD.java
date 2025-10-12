package backend.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import backend.model.fine.CDFineStrategy;

public class CD extends Media {

    public CD(String title, String author) { 
        super(title, author, null, new CDFineStrategy()); 
    }

    @Override
    public void borrow(String username) {
        this.borrowed = true;
        this.borrowerUsername = username;
        this.dueDate = LocalDate.now().plusDays(7); // ✅ 7 أيام فقط
    }

    @Override
    public void returned() {
        this.borrowed = false;
        this.borrowerUsername = null;
        this.dueDate = null;
    }

    @Override
    public boolean isOverdue() {
        return borrowed && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public double calculateFine() {
        if (!isOverdue()) return 0.0;
        long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return fineStrategy.calculateFine((int) overdueDays);
    }
}
