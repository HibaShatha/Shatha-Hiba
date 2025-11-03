package Strategy;

import backend.model.Book;
import backend.strategy.SearchStrategy;
import backend.strategy.TitleSearchStrategy;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TitleSearchStrategy implementation.
 */
class TitleSearchStrategyTest {

    @Test
    void testSearchWithMatches() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Java Programming", "Author A", null));
        books.add(new Book("Python Basics", "Author B", null));
        books.add(new Book("Advanced Java", "Author C", null));

        SearchStrategy strategy = new TitleSearchStrategy();
        List<Book> result = strategy.search(books, "Java");

        assertEquals(2, result.size(), "Should find 2 books with 'Java' in the title");
        assertTrue(result.stream().anyMatch(b -> b.getTitle().equals("Java Programming")));
        assertTrue(result.stream().anyMatch(b -> b.getTitle().equals("Advanced Java")));
    }

    @Test
    void testSearchWithNoMatches() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("C++ Basics", "Author D", null));

        SearchStrategy strategy = new TitleSearchStrategy();
        List<Book> result = strategy.search(books, "Java");

        assertTrue(result.isEmpty(), "Should return empty list if no books match");
    }

    @Test
    void testSearchEmptyList() {
        List<Book> books = new ArrayList<>();
        SearchStrategy strategy = new TitleSearchStrategy();
        List<Book> result = strategy.search(books, "Java");

        assertTrue(result.isEmpty(), "Should return empty list if input list is empty");
    }

    @Test
    void testSearchNullKeyword() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Java Programming", "Author A", null));

        SearchStrategy strategy = new TitleSearchStrategy();
        List<Book> result = strategy.search(books, "");

        assertTrue(result.isEmpty(), "Should return empty list if keyword is empty");
    }
}
