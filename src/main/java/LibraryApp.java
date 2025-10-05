import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            AdminService adminService = new AdminService();
            UserService userService = new UserService();
            BookService bookService = new BookService(adminService);

            while (true) {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1- Admin Login");
                System.out.println("2- User Login");
                System.out.println("3- Create Admin Account");
                System.out.println("4- Create User Account");
                System.out.println("5- Forgot Admin Password");
                System.out.println("6- Forgot User Password");
                System.out.println("7- Exit");
                System.out.print("Choose an option: ");
                int mainOption = scanner.nextInt();
                scanner.nextLine();

                switch (mainOption) {
                    case 1 -> {
                        System.out.print("Admin Username: ");
                        String username = scanner.nextLine();
                        System.out.print("Admin Password: ");
                        String password = scanner.nextLine();
                        if (adminService.login(username, password)) {
                            adminMenu(scanner, adminService, bookService);
                        }
                    }
                    case 2 -> {
                        System.out.print("User Username: ");
                        String username = scanner.nextLine();
                        System.out.print("User Password: ");
                        String password = scanner.nextLine();
                        if (userService.login(username, password)) {
                            userMenu(scanner, userService, bookService);
                        }
                    }
                    case 3 -> {
                        System.out.print("New Admin Username: ");
                        String username = scanner.nextLine();
                        System.out.print("New Admin Password: ");
                        String password = scanner.nextLine();
                        adminService.createAccount(username, password);
                    }
                    case 4 -> {
                        System.out.print("New User Username: ");
                        String username = scanner.nextLine();
                        System.out.print("New User Password: ");
                        String password = scanner.nextLine();
                        userService.createAccount(username, password);
                    }
                    case 5 -> {
                        System.out.print("Admin Username: ");
                        String username = scanner.nextLine();
                        System.out.print("New Password: ");
                        String newPassword = scanner.nextLine();
                        adminService.resetPassword(username, newPassword);
                    }
                    case 6 -> {
                        System.out.print("User Username: ");
                        String username = scanner.nextLine();
                        System.out.print("New Password: ");
                        String newPassword = scanner.nextLine();
                        userService.resetPassword(username, newPassword);
                    }
                    case 7 -> System.exit(0);
                    default -> System.out.println("Invalid option!");
                }
            }
        }
    }

    private static void adminMenu(Scanner scanner, AdminService adminService, BookService bookService) {
        ReminderService reminderService = new ReminderService(bookService);
        while (adminService.isLoggedIn()) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1- Add Book");
            System.out.println("2- Search Book");
            System.out.println("3- Show All Books");
            System.out.println("4- Show Overdue Books");
            System.out.println("5- Send Reminder Emails");
            System.out.println("6- Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            String username = adminService.getLoggedInUsername();

            switch (option) {
                case 1 -> {
                    System.out.print("Book Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Author: ");
                    String author = scanner.nextLine();
                    System.out.print("ISBN: ");
                    String isbn = scanner.nextLine();
                    bookService.addBook(new Book(title, author, isbn));
                }
                case 2 -> {
                    System.out.print("Search keyword: ");
                    String keyword = scanner.nextLine();
                    var results = bookService.searchBook(keyword);
                    if (results.isEmpty()) System.out.println("No books found!");
                    else results.forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthor() +
                                                                " | ISBN: " + b.getIsbn() +
                                                                " | Borrowed: " + (b.isBorrowed() ?
                                                                    "Yes, Due: " + b.getDueDate() + ", By: " + b.getBorrowerUsername() +
                                                                    (b.isOverdue() ? ", Fine: $" + b.calculateFine() : "") :
                                                                    "No")));
                }
                case 3 -> bookService.getAllBooks().forEach(b -> System.out.println(
                        b.getTitle() + " | " + b.getAuthor() + " | ISBN: " + b.getIsbn() +
                        " | Borrowed: " + (b.isBorrowed() ? "Yes, Due: " + b.getDueDate() + ", By: " + b.getBorrowerUsername() : "No")
                ));
             
                case 4 -> bookService.getOverdueBooks().forEach(b -> System.out.println(
                        b.getTitle() + " | Borrowed by: " + b.getBorrowerUsername() +
                        " | Due: " + b.getDueDate() + " | Fine: $" + b.calculateFine()
                ));
             
                case 5 -> reminderService.sendReminders();
                case 6 -> adminService.logout();
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private static void userMenu(Scanner scanner, UserService userService, BookService bookService) {
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
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    System.out.print("Search keyword: ");
                    String keyword = scanner.nextLine();
                    var results = bookService.searchBook(keyword);
                    if (results.isEmpty()) System.out.println("No books found!");
                    else results.forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthor() +
                                                                " | ISBN: " + b.getIsbn() +
                                                                " | Borrowed: " + (b.isBorrowed() ?
                                                                    "Yes, Due: " + b.getDueDate() + ", By: " + b.getBorrowerUsername() +
                                                                    (b.isOverdue() ? ", Fine: $" + b.calculateFine() : "") :
                                                                    "No")));
                }
                case 2 -> bookService.getAllBooks().forEach(b -> System.out.println(
                        b.getTitle() + " | " + b.getAuthor() + " | ISBN: " + b.getIsbn() +
                        " | Borrowed: " + (b.isBorrowed() ? "Yes, Due: " + b.getDueDate() + ", By: " + b.getBorrowerUsername() : "No")
                ));
                case 3 -> {
                    System.out.print("Enter ISBN to borrow: ");
                    String isbn = scanner.nextLine();
                    bookService.borrowBook(isbn, username); // US2.1
                }
                case 4 -> {
                    System.out.print("Enter ISBN to return: ");
                    String isbn = scanner.nextLine();
                    bookService.returnBook(isbn, username); // US2.2
                }
                case 5 -> {
                    System.out.print("Enter amount to pay: $");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    bookService.payFine(username, amount); // US2.3
                }
                case 6 -> userService.logout();
                default -> System.out.println("Invalid option!");
            }
        }
    }
}
