package backend.repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import backend.model.Book;

/**
 * Repository class for managing Book data.
 * <p>
 * Provides methods to add, retrieve, search, and update books stored in a CSV file.
 * Automatically creates the file if it does not exist.
 * </p>
 * <p>
 * Only the important methods are documented to keep it clean and professional.
 * </p>
 * 
 * @author Hiba_ibraheem
 * @version 1.0.0
 */
public class BookRepository {

    /** Path to the CSV file storing book data */
    private final String FILE_PATH;

    /** Default constructor using "books.csv" */
    public BookRepository() {
        this("books.csv");
    }

    /**
     * Constructor with custom file path.
     *
     * @param filePath path to the CSV file
     */
    public BookRepository(String filePath) {
        this.FILE_PATH = filePath;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    /** Returns the file path used by this repository */
    public String getFilePath() {
        return FILE_PATH;
    }

    /** Adds a new book to the CSV file */
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
     * Retrieves all books from the CSV file.
     * <p>
     * Parses each line into a Book object, including borrowed status, due date, and borrower.
     * </p>
     *
     * @return list of all books
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
     * Searches for books by keyword in title, author, or ISBN.
     *
     * @param keyword the search keyword
     * @return list of matching books
     */
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

    /**
     * Updates a book's information in the CSV file.
     * <p>
     * Uses a temporary file to safely replace the original file.
     * </p>
     *
     * @param book the Book object with updated information
     * @return true if the book was found and updated; false otherwise
     */
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
