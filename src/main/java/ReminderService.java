import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReminderService {
    private BookService bookService;
    private final String LOG_FILE = "mock_emails.txt";

    public ReminderService(BookService bookService) {
        this.bookService = bookService;
    }

    public void sendReminders() {
        Map<String, List<Book>> overdueByUser = bookService.getOverdueBooks()
                .stream()
                .collect(Collectors.groupingBy(Book::getBorrowerUsername));

        overdueByUser.forEach((username, books) -> {
            String message = "Hello " + username + ", you have " + books.size() + " overdue book(s).";
            System.out.println("[Mock Email] " + message);
            logMockEmail(username, message);
        });

        if (overdueByUser.isEmpty()) {
            System.out.println("No overdue books found. No reminders sent.");
        }
    }

    private void logMockEmail(String username, String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write("To: " + username + " | Message: " + message);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
