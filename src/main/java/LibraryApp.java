import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            AdminService adminService = new AdminService();
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
                    case 1 -> {
                        System.out.print("Username: ");
                        String username = scanner.nextLine();
                        System.out.print("Password: ");
                        String password = scanner.nextLine();
                        if (adminService.login(username, password)) {
                            bookMenu(scanner, adminService, bookService);
                        }
                    }
                    case 2 -> {
                        System.out.print("New Username: ");
                        String username = scanner.nextLine();
                        System.out.print("New Password: ");
                        String password = scanner.nextLine();
                        adminService.createAccount(username, password);
                    }
                    case 3 -> {
                        System.out.print("Username: ");
                        String username = scanner.nextLine();
                        System.out.print("New Password: ");
                        String newPassword = scanner.nextLine();
                        adminService.resetPassword(username, newPassword);
                    }
                    case 4 -> System.exit(0);
                    default -> System.out.println("Invalid option!");
                }
            }
        }
    }

    private static void bookMenu(Scanner scanner, AdminService adminService, BookService bookService) {
        while (adminService.isLoggedIn()) {
            String username = adminService.getLoggedInUsername();
            double fineBalance = bookService.getUserFineBalance(username);
            System.out.println("\n=== Book Management Menu ===");
            System.out.println("Current fine balance: $" + fineBalance);
            System.out.println("1- Add Book");
            System.out.println("2- Search Book");
            System.out.println("3- Show All Books");
            System.out.println("4- Borrow Book");
            System.out.println("5- Return Book");
            System.out.println("6- Show Overdue Books");
            System.out.println("7- Pay Fine");
            System.out.println("8- Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

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
                case 3 -> {
                    var allBooks = bookService.getAllBooks();
                    if (allBooks.isEmpty()) System.out.println("No books in library.");
                    else allBooks.forEach(b -> System.out.println(b.getTitle() + " by " + b.getAuthor() +
                                                                 " | ISBN: " + b.getIsbn() +
                                                                 " | Borrowed: " + (b.isBorrowed() ?
                                                                     "Yes, Due: " + b.getDueDate() + ", By: " + b.getBorrowerUsername() +
                                                                     (b.isOverdue() ? ", Fine: $" + b.calculateFine() : "") :
                                                                     "No")));
                }
                case 4 -> {
                    System.out.print("Enter ISBN to borrow: ");
                    String isbn = scanner.nextLine();
                    bookService.borrowBook(isbn);
                }
                case 5 -> {
                    System.out.print("Enter ISBN to return: ");
                    String isbn = scanner.nextLine();
                    bookService.returnBook(isbn);
                }
                case 6 -> {
                    var overdueBooks = bookService.getOverdueBooks();
                    if (overdueBooks.isEmpty()) System.out.println("No overdue books found!");
                    else overdueBooks.forEach(b -> System.out.println("Overdue: " + b.getTitle() + " by " + b.getAuthor() +
                                                                     " | ISBN: " + b.getIsbn() +
                                                                     " | Due: " + b.getDueDate() +
                                                                     " | Borrowed by: " + b.getBorrowerUsername() +
                                                                     " | Fine: $" + b.calculateFine()));
                }
                case 7 -> {
                    System.out.println("Your current fine balance: $" + bookService.getUserFineBalance(username));
                    System.out.print("Enter amount to pay: $");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    bookService.payFine(username, amount);
                }
                case 8 -> adminService.logout();
                default -> System.out.println("Invalid option!");
            }
        }
    }
}