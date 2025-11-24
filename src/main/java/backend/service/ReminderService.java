// File: src/backend/service/ReminderService.java

package backend.service;

import backend.model.Book;
import backend.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReminderService {
    private final MediaService mediaService;
    private final UserService userService;
    protected final List<Observer> observers = new ArrayList<>();

    public ReminderService(MediaService mediaService, UserService userService) {
        this.mediaService = mediaService;
        this.userService = userService;

        // Real email notification
        observers.add(new RealEmailNotifier());

        // Fake SMS notification (you can disable it if you don't need it)
        observers.add(new SMSNotifier());
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void sendReminders() {
        Map<String, List<Book>> overdueByUser = mediaService.getOverdueBooks()
                .stream()
                .collect(Collectors.groupingBy(Book::getBorrowerUsername));

        if (overdueByUser.isEmpty()) {
            System.out.println("No overdue books. No reminders were sent.");
            return;
        }

        overdueByUser.forEach((username, books) -> {
            User user = userService.findByUsername(username);
            if (user != null) {
                String message = "You have " + books.size() +
                    (books.size() == 1 ? " overdue book." : " overdue books.");
                
                for (Observer observer : observers) {
                    observer.notify(user, message);
                }
            }
        });

        System.out.println("Reminders sent successfully!");
    }
}
