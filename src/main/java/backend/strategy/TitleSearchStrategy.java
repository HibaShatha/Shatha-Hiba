package backend.strategy;

import java.util.ArrayList;
import java.util.List;

import backend.model.Book;

public class TitleSearchStrategy implements SearchStrategy {
	/**
     * Searches the list of books for titles containing the keyword (case-insensitive).
     *
     * @param books   the list of books to search
     * @param keyword the keyword to match in book titles
     * @return a list of books with titles matching the keyword
     * @author Shatha_Dweikat
     */
	@Override
	public List<Book> search(List<Book> books, String keyword) {
	    List<Book> result = new ArrayList<>();
	    
	    // Return empty list if keyword is null or empty
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return result;
	    }

	    for (Book book : books) {
	        if (book.getTitle() != null && book.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
	            result.add(book);
	        }
	    }
	    return result;
	}

}