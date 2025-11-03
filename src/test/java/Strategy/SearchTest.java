package Strategy;

import backend.model.Book;
import backend.strategy.AuthorSearchStrategy;
import backend.strategy.Search;
import backend.strategy.TitleSearchStrategy;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    @Test
    void testPerformSearchWithTitleStrategy() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Java Programming", "Author A", "111"));
        books.add(new Book("Python Basics", "Author B", "222"));

        Search search = new Search(new TitleSearchStrategy());
        List<Book> result = search.performSearch(books, "Java");

        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).getTitle());
    }

    @Test
    void testSetStrategyAtRuntime() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", "123"));
        books.add(new Book("Book B", "Author Y", "456"));

        Search search = new Search(new TitleSearchStrategy());
        search.setStrategy(new AuthorSearchStrategy());
        List<Book> result = search.performSearch(books, "Author Y");

        assertEquals(1, result.size());
        assertEquals("Author Y", result.get(0).getAuthor());
    }

    @Test
    void testPerformSearchWithoutStrategy() {
        List<Book> books = new ArrayList<>();
        Search search = new Search(null);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            search.performSearch(books, "Java");
        });

        assertEquals("Search strategy is not set!", exception.getMessage());
    }
}
