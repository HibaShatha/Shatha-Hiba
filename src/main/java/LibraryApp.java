
import java.util.List;
import java.util.Scanner;

import backend.model.Book;
import backend.model.CD;
import backend.model.Librarian;
import backend.model.Media;
import backend.repository.BookRepository;
import backend.repository.CDRepository;
import backend.repository.FineRepository;
import backend.repository.UserRepository;
import backend.service.AdminService;
import backend.service.MediaService;
import backend.service.UserService;
import backend.strategy.AuthorSearchStrategy;
import backend.strategy.IsbnSearchStrategy;
import backend.strategy.Search;
import backend.strategy.SearchStrategy;
import backend.strategy.TitleSearchStrategy;
import backend.service.EmailService;
import backend.service.ReminderService;


/**
 * Main entry point for the Library Application.
 * Provides a console-based interface for Admins and Users
 * to manage books, CDs, accounts, and fines.
 */

public class LibraryApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // إنشاء الـ repositories
        BookRepository bookRepo = new BookRepository();
        CDRepository cdRepo = new CDRepository();
        FineRepository fineRepo = new FineRepository();

        // تشغيل الـ Librarian thread
        Librarian librarian = new Librarian(bookRepo, cdRepo, fineRepo);
        Thread librarianThread = new Thread(librarian);
        librarianThread.start();

        AdminService adminService = new AdminService();
        UserService userService = new UserService();
        MediaService mediaService = new MediaService(adminService);
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
                    loginMenu(scanner, adminService, userService, mediaService);
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

    private static void loginMenu(Scanner scanner, AdminService adminService, UserService userService, MediaService mediaService) {
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

            switch (loginType) {
                case 1:
                    if (adminService.login(username, password)) {
                        adminMenu(scanner, adminService, mediaService, userService);
                    } else {
                        System.out.println("Try again.");
                    }
                    break;
                case 2:
                    if (userService.login(username, password)) {
                        userMenu(scanner, userService, mediaService);
                    } else {
                        System.out.println("Try again.");
                    }
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
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
            String phoneNumber = "";
            if (accountType == 2) {
                System.out.print("Email: ");
                email = scanner.nextLine();
                System.out.print("Phone Number: ");
                phoneNumber = scanner.nextLine();
            }

            switch (accountType) {
                case 1:
                    adminService.createAccount(username, password);
                    break;
                case 2:
                    userService.createAccount(username, password, email, phoneNumber);
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
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

            switch (type) {
                case 1:
                    adminService.resetPassword(username, newPassword);
                    System.out.println("Admin password reset!");
                    break;
                case 2:
                    userService.resetPassword(username, newPassword);
                    System.out.println("User password reset!");
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

    private static void adminMenu(Scanner scanner, AdminService adminService, MediaService mediaService, UserService userService) {
        ReminderService reminderService = new ReminderService(mediaService, userService);

        while (adminService.isLoggedIn()) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1- Add Book");
            System.out.println("2- Add CD");
            System.out.println("3- Search Book/CD");
            System.out.println("4- Show All Books");
            System.out.println("5- Show All CDs");
            System.out.println("6- Show Overdue Books/CDs");
            System.out.println("7- Send Reminder");
            System.out.println("8- Unregister User");
            System.out.println("9- Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt(); 
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Book Title: "); 
                    String title = scanner.nextLine();
                    System.out.print("Author: "); 
                    String author = scanner.nextLine();
                    System.out.print("Starting ISBN: "); 
                    int startIsbn = Integer.parseInt(scanner.nextLine());
                    System.out.print("Quantity: "); 
                    int qty = Integer.parseInt(scanner.nextLine());
                    mediaService.addBookWithStartingIsbn(title, author, startIsbn, qty);
                    break;
                case 2:
                    System.out.print("CD Title: "); 
                    String cdTitle = scanner.nextLine();
                    System.out.print("Artist: "); 
                    String artist = scanner.nextLine();
                    mediaService.addCD(cdTitle, artist);
                    break;
                case 3:
                    System.out.print("Search Book or CD (enter 'book' or 'cd'): "); 
                    String type = scanner.nextLine().toLowerCase();

                    if (type.equals("book")) {
                        System.out.print("Search by (title/author/isbn): ");
                        String mode = scanner.nextLine().toLowerCase();

                        System.out.print("Enter keyword: ");
                        String keyword = scanner.nextLine();

                        // اختيار الاستراتيجية حسب اختيار المستخدم
                        SearchStrategy strategy = null;
                        switch (mode) {
                            case "title":
                                strategy = new TitleSearchStrategy();
                                break;
                            case "author":
                                strategy = new AuthorSearchStrategy();
                                break;
                            case "isbn":
                                strategy = new IsbnSearchStrategy();
                                break;
                            default:
                                System.out.println("Invalid search type!");
                                break;
                        }

                        // إنشاء الـ context وتنفيذ البحث
                        Search searchContext = new Search(strategy);
                        List<Book> results = mediaService.searchBooks(searchContext, keyword);

                        if (results.isEmpty()) System.out.println("No books found!");
                        else for (Book b : results) System.out.println(formatMediaInfo(b));

                    } else if (type.equals("cd")) {
                        System.out.print("Enter keyword: ");
                        String keyword = scanner.nextLine();

                        List<CD> cds = mediaService.searchCD(keyword);
                        if (cds.isEmpty()) System.out.println("No CDs found!");
                        else for (CD c : cds) System.out.println(formatMediaInfo(c));

                    } else {
                        System.out.println("Invalid type!");
                    }
                    break;


                case 4:
                    for (Book b : mediaService.getAllBooks()) System.out.println(formatMediaInfo(b));
                    break;
                case 5:
                    for (CD c : mediaService.getAllCDs()) System.out.println(formatMediaInfo(c));
                    break;
                case 6:
                    for (Book b : mediaService.getOverdueBooks()) System.out.println(formatMediaInfo(b));
                    for (CD c : mediaService.getOverdueCDs()) System.out.println(formatMediaInfo(c));
                    break;
                case 7:
                    reminderService.sendReminders();
                    break;
                case 8:
                    System.out.print("Enter username to unregister: ");
                    String username = scanner.nextLine();
                    adminService.unregisterUser(username, new UserRepository(), mediaService);
                    break;
                case 9:
                    adminService.logout();
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

    private static void userMenu(Scanner scanner, UserService userService, MediaService mediaService) {
        EmailService emailService = new EmailService();
        String username = userService.getLoggedInUsername();

        while (userService.isLoggedIn()) {
            double fineBalance = mediaService.getUserFineBalance(username);
            System.out.println("\n=== User Menu ===");
            System.out.println("Current fine balance: $" + fineBalance);
            System.out.println("1- Search Book/CD");
            System.out.println("2- Show All Books");
            System.out.println("3- Show All CDs");
            System.out.println("4- Borrow Book");
            System.out.println("5- Borrow CD");
            System.out.println("6- Return Book");
            System.out.println("7- Return CD");
            System.out.println("8- Pay Fine");
            System.out.println("9- Logout");
            System.out.println("10- Messages");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt(); 
            scanner.nextLine();

            switch (option) {
            case 1:
                System.out.print("Search Book or CD (enter 'book' or 'cd'): ");
                String type = scanner.nextLine().toLowerCase();

                if (type.equals("book")) {
                    System.out.print("Search by (title/author/isbn): ");
                    String mode = scanner.nextLine().toLowerCase();

                    System.out.print("Enter keyword: ");
                    String keyword = scanner.nextLine();

                    // تهيئة الاستراتيجية بـ null
                    SearchStrategy strategy = null;

                    switch (mode) {
                        case "title":
                            strategy = new TitleSearchStrategy();
                            break;
                        case "author":
                            strategy = new AuthorSearchStrategy();
                            break;
                        case "isbn":
                            strategy = new IsbnSearchStrategy();
                            break;
                        default:
                            System.out.println("Invalid search type!");
                            break;
                    }

                    // تأكد من أنها مش null قبل الاستخدام
                    if (strategy != null) {
                        Search searchContext = new Search(strategy);
                        List<Book> results = mediaService.searchBooks(searchContext, keyword);

                        if (results.isEmpty()) System.out.println("No books found!");
                        else for (Book b : results) System.out.println(formatMediaInfo(b));
                    } else {
                        System.out.println("Search cancelled due to invalid type.");
                    }

                } else if (type.equals("cd")) {
                    System.out.print("Enter keyword: ");
                    String keyword = scanner.nextLine();

                    // البحث عن CDs كما هو
                    List<CD> cds = mediaService.searchCD(keyword);
                    if (cds.isEmpty()) System.out.println("No CDs found!");
                    else for (CD c : cds) System.out.println(formatMediaInfo(c));

                } else {
                    System.out.println("Invalid type!");
                }
                break;

                case 2:
                    for (Book b : mediaService.getAllBooks()) System.out.println(formatMediaInfo(b));
                    break;
                case 3:
                    for (CD c : mediaService.getAllCDs()) System.out.println(formatMediaInfo(c));
                    break;
                case 4:
                    System.out.print("Enter ISBN to borrow: ");
                    String isbn = scanner.nextLine();
                    mediaService.borrowBook(isbn, username);
                    break;
                case 5:
                    System.out.print("Enter CD title to borrow: ");
                    String cdTitle = scanner.nextLine();
                    mediaService.borrowCD(cdTitle, username);
                    break;
                case 6:
                    System.out.print("Enter ISBN to return: ");
                    String isbnReturn = scanner.nextLine();
                    mediaService.returnBook(isbnReturn, username);
                    break;
                case 7:
                    System.out.print("Enter CD title to return: ");
                    String cdReturn = scanner.nextLine();
                    mediaService.returnCD(cdReturn, username);
                    break;
                case 8:
                    System.out.print("Enter amount to pay: $");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    mediaService.payFine(username, amount);
                    break;
                case 9:
                    userService.logout();
                    break;
                case 10:
                    List<String> messages = emailService.getMessagesForEmail(userService.getLoggedInUserEmail());
                    if (messages.isEmpty()) System.out.println("No messages found!");
                    else {
                        System.out.println("=== Inbox ===");
                        for (String msg : messages) System.out.println(msg);
                    }
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

    private static String formatMediaInfo(Media m) {
        return m.getTitle() + " (" + m.getMediaType() + ")" + "ISBN : " +m.getIsbn()+
               " | Borrowed: " + (m.isBorrowed() ? 
               "Yes, Due: " + m.getDueDate() + ", By: " + m.getBorrowerUsername() +
               (m.isOverdue() ? ", Fine: $" + m.calculateFine() : "") : "No");
    }
}


