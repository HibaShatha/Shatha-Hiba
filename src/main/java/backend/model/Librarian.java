
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
    protected volatile boolean running = true; // end condition

    public Librarian(BookRepository bookRepo, CDRepository cdRepo, FineRepository fineRepo) {
        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
        this.fineRepo = fineRepo;
    }

    @Override
    public void run() {
        while (running) { 
            updateOverdueFinesSafely(); 
            try {
                Thread.sleep(24 * 60 * 60 * 1000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Librarian thread interrupted, stopping.");
                running = false; 
            }
        }
    }

    private void updateOverdueFinesSafely() {
        updateOverdueFines();
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

    // طريقة لإيقاف الخيط من خارج الكلاس
    public void stop() {
        running = false;
    }
}
