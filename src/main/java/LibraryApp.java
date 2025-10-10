import java.util.List;
import java.util.Scanner;

import backend.model.Book;
import backend.repository.UserRepository;
import backend.service.AdminService;
import backend.service.BookService;
import backend.service.EmailService;
import backend.service.ReminderService;
import backend.service.UserService;

public class LibraryApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            AdminService adminService = new AdminService();
            UserService userService = new UserService();
            BookService bookService = new BookService(adminService);

            while (true) {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1- Login");
                System.out.println("2- Create Account");
                System.out.println("3- Forgot Password");
                System.out.println("4- Exit");
                System.out.print("Choose an option: ");
                int mainOption = scanner.nextInt();
                scanner.nextLine();

                switch (mainOption) {
                    case 1:
                        loginMenu(scanner, adminService, userService, bookService);
                        break;
                    case 2:
                        createAccountMenu(scanner, adminService, userService);
                        break;
                    case 3:
                        forgotPasswordMenu(scanner, adminService, userService);
                        break;
                    case 4:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option!");
                        break;
                }
            }
        }
    }

    private static void loginMenu(Scanner scanner, AdminService adminService, UserService userService, BookService bookService) {
        while (true) {
            System.out.println("\n=== Login Menu ===");
            System.out.println("1- Admin");
            System.out.println("2- User");
            System.out.println("3- Back to Main Menu");
            System.out.print("Choose login type: ");
            int loginType = scanner.nextInt();
            scanner.nextLine();

            if (loginType == 3) break;

            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            if (loginType == 1 && adminService.login(username, password)) {
                adminMenu(scanner, adminService, bookService, userService);
                break;
            } else if (loginType == 2 && userService.login(username, password)) {
                userMenu(scanner, userService, bookService);
                break;
            } else {
                System.out.println("Invalid credentials or option! Try again.");
            }
        }
    }

    private static void createAccountMenu(Scanner scanner, AdminService adminService, UserService userService) {
        while (true) {
            System.out.println("\n=== Create Account Menu ===");
            System.out.println("1- Admin");
            System.out.println("2- User");
            System.out.println("3- Back to Main Menu");
            System.out.print("Choose account type: ");
            int accountType = scanner.nextInt();
            scanner.nextLine();

            if (accountType == 3) break;

            System.out.print("New Username: ");
            String username = scanner.nextLine();
            System.out.print("New Password: ");
            String password = scanner.nextLine();
            String email = "";
            if (accountType == 2) {
                System.out.print("Email: ");
                email = scanner.nextLine();
            }

            if (accountType == 1) {
                adminService.createAccount(username, password);
            } else if (accountType == 2) {
                userService.createAccount(username, password, email);
            } else {
                System.out.println("Invalid option! Try again.");
            }
        }
    }

    private static void forgotPasswordMenu(Scanner scanner, AdminService adminService, UserService userService) {
        while (true) {
            System.out.println("\n=== Forgot Password Menu ===");
            System.out.println("1- Admin");
            System.out.println("2- User");
            System.out.println("3- Back to Main Menu");
            System.out.print("Choose account type: ");
            int type = scanner.nextInt();
            scanner.nextLine();

            if (type == 3) break;

            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("New Password: ");
            String newPassword = scanner.nextLine();

            if (type == 1) {
                adminService.resetPassword(username, newPassword);
                System.out.println("Admin password reset successfully!");
            } else if (type == 2) {
                userService.resetPassword(username, newPassword);
                System.out.println("User password reset successfully!");
            } else {
                System.out.println("Invalid option! Try again.");
            }
        }
    }

    private static void adminMenu(Scanner scanner, AdminService adminService, BookService bookService, UserService userService) {
        ReminderService reminderService = new ReminderService(bookService, userService);

        while (adminService.isLoggedIn()) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1- Add Book");
            System.out.println("2- Search Book");
            System.out.println("3- Show All Books");
            System.out.println("4- Show Overdue Books");
            System.out.println("5- Send Reminder Emails");
            System.out.println("6- Unregister User");
            System.out.println("7- Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Book Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Author: ");
                    String author = scanner.nextLine();
                    System.out.print("ISBN: ");
                    String isbn = scanner.nextLine();
                    bookService.addBook(new Book(title, author, isbn));
                    break;
                case 2:
                    System.out.print("Search keyword: ");
                    String keyword = scanner.nextLine();
                    List<Book> results = bookService.searchBook(keyword);
                    if (results.isEmpty()) System.out.println("No books found!");
                    else results.forEach(b -> System.out.println(formatBookInfo(b)));
                    break;
                case 3:
                    bookService.getAllBooks().forEach(b -> System.out.println(formatBookInfo(b)));
                    break;
                case 4:
                    bookService.getOverdueBooks().forEach(b -> System.out.println(formatBookInfo(b)));
                    break;
                case 5:
                    reminderService.sendReminders();
                    break;
                case 6:
                    System.out.print("Enter username to unregister: ");
                    String username = scanner.nextLine();
                    adminService.unregisterUser(username, new UserRepository(), bookService);
                    break;
                case 7:
                    adminService.logout();
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

    private static void userMenu(Scanner scanner, UserService userService, BookService bookService) {
        EmailService emailService = new EmailService();
        String username = userService.getLoggedInUsername();
        while (userService.isLoggedIn()) {
            double fineBalance = bookService.getUserFineBalance(username);
            System.out.println("\n=== User Menu ===");
            System.out.println("Current fine balance: $" + fineBalance);
            System.out.println("1- Search Book");
            System.out.println("2- Show All Books");
            System.out.println("3- Borrow Book");
            System.out.println("4- Return Book");
            System.out.println("5- Pay Fine");
            System.out.println("6- Logout");
            System.out.println("7- Messages");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Search keyword: ");
                    String keyword = scanner.nextLine();
                    List<Book> results = bookService.searchBook(keyword);
                    if (results.isEmpty()) System.out.println("No books found!");
                    else results.forEach(b -> System.out.println(formatBookInfo(b)));
                    break;
                case 2:
                    bookService.getAllBooks().forEach(b -> System.out.println(formatBookInfo(b)));
                    break;
                case 3:
                    System.out.print("Enter ISBN to borrow: ");
                    String isbn = scanner.nextLine();
                    bookService.borrowBook(isbn, username);
                    break;
                case 4:
                    System.out.print("Enter ISBN to return: ");
                    String returnIsbn = scanner.nextLine();
                    bookService.returnBook(returnIsbn, username);
                    break;
                case 5:
                    System.out.print("Enter amount to pay: $");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    bookService.payFine(username, amount);
                    break;
                case 6:
                    userService.logout();
                    break;
                case 7:
                    List<String> messages = emailService.getMessagesForEmail(userService.getLoggedInUserEmail());
                    if (messages.isEmpty()) {
                        System.out.println("No messages found!");
                    } else {
                        System.out.println("=== Inbox ===");
                        messages.forEach(System.out::println);
                    }
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

    private static String formatBookInfo(Book b) {
        return b.getTitle() + " by " + b.getAuthor() +
                " | ISBN: " + b.getIsbn() +
                " | Borrowed: " + (b.isBorrowed() ? 
                "Yes, Due: " + b.getDueDate() + ", By: " + b.getBorrowerUsername() +
                (b.isOverdue() ? ", Fine: $" + b.calculateFine() : "") :
                "No");
    }
}