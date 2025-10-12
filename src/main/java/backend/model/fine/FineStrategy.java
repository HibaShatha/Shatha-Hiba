package backend.model.fine;

public interface FineStrategy {
    double calculateFine(int overdueDays);
}
