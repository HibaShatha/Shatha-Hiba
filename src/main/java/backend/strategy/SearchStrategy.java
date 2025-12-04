package backend.strategy;
import java.util.ArrayList;
import backend.model.Book;
import java.util.List;

/**
 * Interface representing a strategy for searching books.
 * Different implementations can search by title, author, or other criteria.
 */
public interface SearchStrategy {

    /**
     * Searches a list of books for entries that match the given keyword.
     *
     * @param books   the list of books to search
     * @param keyword the keyword to search for
     * @return a list of books that match the keyword
     * @author Shatha_Dweikat
     */
    List<Book> search(List<Book> books, String keyword);
}
