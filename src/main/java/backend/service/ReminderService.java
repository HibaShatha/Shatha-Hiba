package backend.service;

import backend.model.Book;
import backend.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Service for sending reminders to users about overdue books.
 * Uses Observer pattern to notify via multiple channels (Email, SMS).
 * 
 * @author Shatha_Dweikat
 * @version 2.0 // updated after adding Observer pattern for multi-channel reminders
 */
public class ReminderService {
    private MediaService bookService;
    private UserService userService;
    public List<Observer> observers = new ArrayList<>();

    public ReminderService(MediaService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
        addObserver(new EmailNotifier());
        addObserver(new SMSNotifier());
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
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
                String message = "you have " + books.size() + " overdue book(s).";
                for (Observer observer : observers) {
                    observer.notify(user, message);
                }
            }
        });
    }
}