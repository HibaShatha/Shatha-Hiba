package Strategy;


import backend.model.Book;
import backend.strategy.AuthorSearchStrategy;
import backend.strategy.SearchStrategy;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AuthorSearchStrategyTest {

    @Test
    void testSearchWithMatches() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", null));
        books.add(new Book("Book B", "Author Y", null));
        books.add(new Book("Book C", "Author X", null));

        SearchStrategy strategy = new AuthorSearchStrategy();
        List<Book> result = strategy.search(books, "Author X");

        assertEquals(2, result.size());
    }

    @Test
    void testSearchWithNoMatches() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", null));

        SearchStrategy strategy = new AuthorSearchStrategy();
        List<Book> result = strategy.search(books, "Author Z");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchEmptyKeyword() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", null));

        SearchStrategy strategy = new AuthorSearchStrategy();
        List<Book> result = strategy.search(books, "");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchNullKeyword() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", "Author X", null));

        SearchStrategy strategy = new AuthorSearchStrategy();
        List<Book> result = strategy.search(books, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchNullAuthor() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Book A", null, null));

        SearchStrategy strategy = new AuthorSearchStrategy();
        List<Book> result = strategy.search(books, "Author X");

        assertTrue(result.isEmpty());
    }
}
