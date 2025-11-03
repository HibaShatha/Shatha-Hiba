package Strategy;

import backend.model.Book;
import backend.strategy.IsbnSearchStrategy;
import backend.strategy.SearchStrategy;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class IsbnSearchStrategyTest {

    @Test
    void testSearchWithMatch() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", "12345"));
        books.add(new Book("Book B", "Author Y", "67890"));

        SearchStrategy strategy = new IsbnSearchStrategy();
        List<Book> result = strategy.search(books, "12345");

        assertEquals(1, result.size());
        assertEquals("12345", result.get(0).getIsbn());
    }

    @Test
    void testSearchWithNoMatch() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", "12345"));

        SearchStrategy strategy = new IsbnSearchStrategy();
        List<Book> result = strategy.search(books, "00000");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchEmptyKeyword() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", "12345"));

        SearchStrategy strategy = new IsbnSearchStrategy();
        List<Book> result = strategy.search(books, "");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchNullKeyword() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", "12345"));

        SearchStrategy strategy = new IsbnSearchStrategy();
        List<Book> result = strategy.search(books, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchNullIsbn() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", null));

        SearchStrategy strategy = new IsbnSearchStrategy();
        List<Book> result = strategy.search(books, "12345");

        assertTrue(result.isEmpty());
    }
}
