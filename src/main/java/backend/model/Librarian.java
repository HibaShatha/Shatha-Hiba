
package backend.model;

import backend.repository.BookRepository;
import backend.repository.CDRepository;
import backend.repository.FineRepository;

public class Librarian implements Runnable {
    private final BookRepository bookRepo;
    private final CDRepository cdRepo;
    private final FineRepository fineRepo;

    public Librarian(BookRepository bookRepo, CDRepository cdRepo, FineRepository fineRepo) {
        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
        this.fineRepo = fineRepo;
    }

    @Override
    public void run() {
        while (true) {
            // تحديث الغرامات لكل مستخدم
            updateOverdueFines();
            try {
            	 
               Thread.sleep(24 * 60 * 60 * 1000); // 
            	
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateOverdueFines() {
        // كتب متأخرة
        for (Book book : bookRepo.getAllBooks()) {
            if (book.isOverdue()) {
                double fine = book.calculateFine();
                double currentBalance = fineRepo.getFineBalance(book.getBorrowerUsername());
                fineRepo.updateFineBalance(book.getBorrowerUsername(), currentBalance + fine);
            }
        }

        // CDs متأخرة
        for (CD cd : cdRepo.getAllCDs()) {
            if (cd.isOverdue()) {
                double fine = cd.calculateFine();
                double currentBalance = fineRepo.getFineBalance(cd.getBorrowerUsername());
                fineRepo.updateFineBalance(cd.getBorrowerUsername(), currentBalance + fine);
            }
        }
    }
}
