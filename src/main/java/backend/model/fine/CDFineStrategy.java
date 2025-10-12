package backend.model.fine;

public class CDFineStrategy implements FineStrategy {
    @Override
    public double calculateFine(int overdueDays) {
    	 return 20;   
    	 }
}
