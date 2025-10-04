

import java.util.List;

public class BookService {
    private BookRepository bookRepo = new BookRepository();
    private AdminService adminService;

    public BookService(AdminService adminService) {
        this.adminService = adminService;
    }

    public void addBook(Book book) {
        if (!adminService.isLoggedIn()) {
            System.out.println("Please login as admin first!");
            return;
        }
        bookRepo.addBook(book);
        System.out.println("Book added successfully: " + book.getTitle());
    }

    public List<Book> searchBook(String keyword) {
        return bookRepo.search(keyword);
    }

    public List<Book> getAllBooks() {
        return bookRepo.getAllBooks();
    }
}
