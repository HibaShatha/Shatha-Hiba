package backend.repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import backend.model.Book;
import backend.strategy.Search; // ✅
import backend.strategy.SearchStrategy; // ✅

/**
 * Repository class for managing Book data.
 * Provides methods to add, retrieve, and update books stored in a CSV file.
 * Uses Strategy Pattern for flexible search.
 */
public class BookRepository {

    private final String FILE_PATH;

    public BookRepository() {
        this("books.csv");
    }

    public BookRepository(String filePath) {
        this.FILE_PATH = filePath;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public String getFilePath() {
        return FILE_PATH;
    }

    public void addBook(Book book) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String dueDateStr = book.getDueDate() != null ? book.getDueDate().toString() : "";
            String borrowerUsername = book.getBorrowerUsername() != null ? book.getBorrowerUsername() : "";
            bw.write(book.getTitle() + "," + book.getAuthor() + "," + book.getIsbn() + "," +
                     book.isBorrowed() + "," + dueDateStr + "," + borrowerUsername);
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                Book book = new Book(parts[0], parts[1], parts[2]);
                book.borrowed = Boolean.parseBoolean(parts[3]);

                if (parts.length > 4 && !parts[4].isEmpty()) {
                    try { book.setDueDate(LocalDate.parse(parts[4])); } catch (Exception e) { book.setDueDate(null); }
                }

                if (parts.length > 5 && !parts[5].isEmpty()) {
                    book.borrowerUsername = parts[5];
                }

                books.add(book);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return books;
    }

    /**
     * Performs a search using the given strategy.
     *
     * @param searchContext the Search context containing a strategy
     * @param keyword the keyword to search for
     * @return list of matching books
     */
    public List<Book> searchBooks(Search searchContext, String keyword) {
        return searchContext.performSearch(getAllBooks(), keyword);
    }

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
        } catch (IOException e) { e.printStackTrace(); return false; }
    }
}
