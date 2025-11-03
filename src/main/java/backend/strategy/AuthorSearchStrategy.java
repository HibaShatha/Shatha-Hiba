package backend.strategy;


import java.util.ArrayList;
import java.util.List;
import backend.model.Book;

public class AuthorSearchStrategy implements SearchStrategy {

    @Override
    public List<Book> search(List<Book> books, String keyword) {
        List<Book> results = new ArrayList<>();

        // Return empty list if keyword is null or empty
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        for (Book b : books) {
            // Skip books with null author
            if (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }
}
