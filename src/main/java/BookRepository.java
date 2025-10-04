
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    private final String FILE_PATH = "books.csv";

    public BookRepository() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    // إضافة كتاب جديد
    public void addBook(Book book) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(book.getTitle() + "," + book.getAuthor() + "," + book.getIsbn() + "," + book.isBorrowed());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // استرجاع كل الكتب
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Book book = new Book(parts[0], parts[1], parts[2]);
                book.borrowed = Boolean.parseBoolean(parts[3]); // تحديث حالة الاستعارة
                books.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    // البحث عن كتاب بالعنوان أو المؤلف أو ISBN
    public List<Book> search(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book book : getAllBooks()) {
            if (book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                book.getIsbn().equalsIgnoreCase(keyword)) {
                results.add(book);
            }
        }
        return results;
    }
}
