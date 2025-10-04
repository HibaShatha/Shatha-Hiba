
public class Book {
    private String title;
    private String author;
    private String isbn;
    public boolean borrowed; 

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = false;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isBorrowed() { return borrowed; }

    public void borrow() { this.borrowed = true; }
    public void returned() { this.borrowed = false; }
}