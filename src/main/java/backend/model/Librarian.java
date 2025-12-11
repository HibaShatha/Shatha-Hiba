
package backend.model;

import backend.repository.BookRepository;
import backend.repository.CDRepository;
import backend.repository.FineRepository;
/**
 * Represents a librarian that periodically updates overdue fines for books and CDs.
 * <p>
 * This class implements {@link Runnable} and can be run in a separate thread to
 * automatically update fines daily.
 * </p>
 *
 * @author Shatha_Dweikat
 * @version 1.0
 */
public class Librarian implements Runnable {
    private final BookRepository bookRepo;
    private final CDRepository cdRepo;
    private final FineRepository fineRepo;
    private volatile boolean running = true; // flag لإنهاء loop

    public Librarian(BookRepository bookRepo, CDRepository cdRepo, FineRepository fineRepo) {
        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
        this.fineRepo = fineRepo;
    }

    @Override
    public void run() {
        runDailyFineUpdate();
    }

    public void stop() {
        running = false;
    }

    private void runDailyFineUpdate() {
        while (running) {
            updateOverdueFines();
            try {
                Thread.sleep(24 * 60 * 60 * 1000); // يوم كامل
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void updateOverdueFines() {
        for (Book book : bookRepo.getAllBooks()) {
            if (book.isOverdue()) {
                double fine = book.calculateFine();
                double currentBalance = fineRepo.getFineBalance(book.getBorrowerUsername());
                fineRepo.updateFineBalance(book.getBorrowerUsername(), currentBalance + fine);
            }
        }
        for (CD cd : cdRepo.getAllCDs()) {
            if (cd.isOverdue()) {
                double fine = cd.calculateFine();
                double currentBalance = fineRepo.getFineBalance(cd.getBorrowerUsername());
                fineRepo.updateFineBalance(cd.getBorrowerUsername(), currentBalance + fine);
            }
        }
    }
    
 // فقط للتيست
    public void runDailyFineUpdateIterationForTest() {
        updateOverdueFines();
    }

}
