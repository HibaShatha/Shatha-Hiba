import java.io.*;
import java.time.LocalDate;
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

    // Add a new book to the CSV file
    public void addBook(Book book) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String dueDateStr = book.getDueDate() != null ? book.getDueDate().toString() : "";
            String borrowerUsername = book.getBorrowerUsername() != null ? book.getBorrowerUsername() : "";
            bw.write(book.getTitle() + "," + book.getAuthor() + "," + book.getIsbn() + "," +
                     book.isBorrowed() + "," + dueDateStr + "," + borrowerUsername);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieve all books from the CSV file
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // -1 للحفاظ على الحقول الفارغة
                if (parts.length < 4) { // تأكد من وجود الحقول الأساسية
                    System.out.println("Invalid line in books.csv: " + line);
                    continue;
                }

                // إنشاء الكتاب
                Book book = new Book(parts[0], parts[1], parts[2]);
                book.borrowed = Boolean.parseBoolean(parts[3]); // طبق حالة المستعير

                // ضبط dueDate إذا موجود
                if (parts.length > 4 && !parts[4].isEmpty()) {
                    try {
                        book.setDueDate(LocalDate.parse(parts[4]));
                    } catch (Exception e) {
                        System.out.println("Error parsing due date in line: " + line);
                        book.setDueDate(null);
                    }
                }

                // ضبط borrowerUsername إذا موجود
                if (parts.length > 5 && !parts[5].isEmpty()) {
                    book.borrowerUsername = parts[5];
                }

                books.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }


    // Search books by title, author, or ISBN
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

    // Update a book's record in the CSV file
    public boolean updateBook(Book book) {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("temp_books.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3 && parts[2].equals(book.getIsbn())) {
                    String dueDateStr = book.getDueDate() != null ? book.getDueDate().toString() : "";
                    String borrowerUsername = book.getBorrowerUsername() != null ? book.getBorrowerUsername() : "";
                    writer.write(book.getTitle() + "," + book.getAuthor() + "," + book.getIsbn() + "," +
                                 book.isBorrowed() + "," + dueDateStr + "," + borrowerUsername);
                    found = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            reader.close();
            writer.close();

            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.out.println("Error updating book file");
                return false;
            }

            return found;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}