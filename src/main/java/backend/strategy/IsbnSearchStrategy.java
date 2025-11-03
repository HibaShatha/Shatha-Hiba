package backend.strategy;

import java.util.ArrayList;
import java.util.List;
import backend.model.Book;

/**
 * Implementation of SearchStrategy that searches books by ISBN.
 */
public class IsbnSearchStrategy implements SearchStrategy {

    @Override
    public List<Book> search(List<Book> books, String keyword) {
        List<Book> results = new ArrayList<>();

        // Return empty list if keyword is null or empty
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        for (Book b : books) {
            // Skip books with null ISBN
            if (b.getIsbn() != null && b.getIsbn().equalsIgnoreCase(keyword)) {
                results.add(b);
            }
        }
        return results;
    }
}
