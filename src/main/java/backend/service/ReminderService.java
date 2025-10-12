package backend.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import backend.model.Book;
import backend.model.User;

public class ReminderService {
    private MediaService bookService;
    private UserService userService;
    private final String LOG_FILE = "mock_emails.txt";

    public ReminderService(MediaService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    public void sendReminders() {
        Map<String, List<Book>> overdueByUser = bookService.getOverdueBooks()
                .stream()
                .collect(Collectors.groupingBy(Book::getBorrowerUsername));

        if (overdueByUser.isEmpty()) {
            System.out.println("No overdue books found. No reminders sent.");
            return;
        }

        overdueByUser.forEach((username, books) -> {
            User user = userService.findByUsername(username);
            if (user != null) {
                String email = user.getEmail();
                String message = "Hello " + username + ", you have " + books.size() + " overdue book(s).";
                System.out.println("[Mock Email to " + email + "] " + message);
                logMockEmail(email, message);
            }
        });
    }

    private void logMockEmail(String email, String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("emails.txt", true))) {
            bw.write("to: " + email + " | message: " + message);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
