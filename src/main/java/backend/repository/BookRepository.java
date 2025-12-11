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
 * <p>
 * Provides methods to add, retrieve, update books stored in a CSV file,
 * and perform searches using the Strategy Pattern.
 * </p>
 * Handles file creation automatically if the CSV file does not exist.
 * 
 * @author Shatha_Dweikat
 * @version 1.0.1
 */
public class BookRepository {
	  /** Path to the CSV file storing book data */
    private final String FILE_PATH;
    /** Default constructor using "books.csv" as the file path */
    public BookRepository() {
        this("files/books.csv");
    }
    /**
     * Constructor with custom file path.
     * <p>
     * Creates the file if it does not exist.
     * </p>
     * 
     * @param filePath the path to the CSV file
     */
    public BookRepository(String filePath) {
        this.FILE_PATH = filePath;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }
    /**
     * Returns the file path of the CSV file.
     * 
     * @return the CSV file path
     */
    public String getFilePath() {
        return FILE_PATH;
    }
    /**
     * Adds a new book to the CSV file.
     * 
     * @param book the Book object to add
     */
    public void addBook(Book book) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String dueDateStr = book.getDueDate() != null ? book.getDueDate().toString() : "";
            String borrowerUsername = book.getBorrowerUsername() != null ? book.getBorrowerUsername() : "";
            bw.write(book.getTitle() + "," + book.getAuthor() + "," + book.getIsbn() + "," +
                     book.isBorrowed() + "," + dueDateStr + "," + borrowerUsername);
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Returns a list of all books in the CSV file.
     * 
     * @return list of Book objects
     */
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
     * Performs a search using the given search context (strategy).
     * 
     * @param searchContext the Search context containing a strategy
     * @param keyword the keyword to search for
     * @return list of matching Book objects
     */
    public List<Book> searchBooks(Search searchContext, String keyword) {
        return searchContext.performSearch(getAllBooks(), keyword);
    }
    /**
     * Updates an existing book in the CSV file.
     * 
     * @param book the Book object with updated data
     * @return true if the book was found and updated; false otherwise
     */
    public boolean updateBook(Book book) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File("files/temp_books.csv");
        boolean found = false;

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;

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

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // حذف الملف القديم وإعادة تسمية الملف المؤقت
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.out.println("Error updating book file");
            return false;
        }

        return found;
    }

}
