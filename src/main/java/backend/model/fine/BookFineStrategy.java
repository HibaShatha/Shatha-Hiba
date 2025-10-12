package backend.model.fine;

public class BookFineStrategy implements FineStrategy {
    @Override
    public double calculateFine(int overdueDays) {
        return 10; // 10 NIS لكل كتاب متأخر
    }
}
