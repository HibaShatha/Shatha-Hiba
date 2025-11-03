package backend.strategy;

import java.util.List;
import backend.model.Book;

/**
 * Context class that uses a SearchStrategy to perform book searches.
 * The strategy can be changed at runtime using setStrategy().
 */
public class Search {

    private SearchStrategy strategy;

    /**
     * Constructs a Search context with the given strategy.
     *
     * @param strategy the initial search strategy to use
     */
    public Search(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Sets or changes the search strategy at runtime.
     *
     * @param strategy the new search strategy
     */
    public void setStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Performs the search using the current strategy.
     *
     * @param books   the list of books to search
     * @param keyword the keyword to search for
     * @return a list of books matching the keyword according to the strategy
     * @throws IllegalStateException if the strategy is not set
     * @author Shatha_Dweikat
     */
    public List<Book> performSearch(List<Book> books, String keyword) {
        if (strategy == null) {
            throw new IllegalStateException("Search strategy is not set!");
        }
        return strategy.search(books, keyword);
    }
}
