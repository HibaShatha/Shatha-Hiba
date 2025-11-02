package backend.service;

import backend.model.Admin;
import backend.repository.AdminRepository;
import backend.repository.UserRepository;
import backend.service.MediaService;

/**
 * Service class for handling admin-related operations.
 * <p>
 * Includes login, logout, account creation, password reset, and user unregistration.
 * Only the key or complex methods are documented.
 * </p>
 * 
 * @author Shatha_Dweikat
 * @version 1.0.0
 */
public class AdminService {
    private AdminRepository repo = new AdminRepository();
    private Admin loggedInAdmin = null;

    /**
     * Attempts to log in an admin with provided credentials.
     *
     * @param username the admin username
     * @param password the admin password
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        Admin admin = repo.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            loggedInAdmin = admin;
            System.out.println("Login successful!");
            return true;
        } else {
            System.out.println("Invalid credentials!");
            return false;
        }
    }

    public void logout() {
        if (loggedInAdmin != null) {
            loggedInAdmin = null;
            System.out.println("Logged out successfully!");
        } else {
            System.out.println("No admin is currently logged in.");
        }
    }

    public boolean isLoggedIn() {
        return loggedInAdmin != null;
    }

    public String getLoggedInUsername() {
        return loggedInAdmin != null ? loggedInAdmin.getUsername() : null;
    }

    public void createAccount(String username, String password) {
        if (repo.findByUsername(username) != null) {
            System.out.println("Username already exists!");
            return;
        }
        repo.addAdmin(new Admin(username, password));
        System.out.println("Account created successfully!");
    }

    public void resetPassword(String username, String newPassword) {
        if (repo.updatePassword(username, newPassword)) {
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Username not found!");
        }
    }

    /**
     * Unregisters a user after performing necessary checks:
     * <ul>
     *     <li>Admin must be logged in.</li>
     *     <li>User must exist.</li>
     *     <li>User must have no active borrowed books.</li>
     *     <li>User must have no unpaid fines.</li>
     * </ul>
     *
     * @param username the username of the user to remove
     * @param userRepo the UserRepository instance for managing users
     * @param bookService the MediaService instance for checking borrowed books and fines
     */
    public void unregisterUser(String username, UserRepository userRepo, MediaService bookService) {
        if (!isLoggedIn()) {
            System.out.println("Please login as admin first!");
            return;
        }

        // Check if user exists
        if (userRepo.findByUsername(username) == null) {
            System.out.println("User with username " + username + " not found!");
            return;
        }

        // Check for active loans
        boolean hasActiveLoans = bookService.getAllBooks().stream()
                                           .anyMatch(b -> b.isBorrowed() && username.equals(b.getBorrowerUsername()));
        if (hasActiveLoans) {
            System.out.println("Cannot unregister: User has active loans. Please return all books first.");
            return;
        }

        // Check for unpaid fines
        double fineBalance = bookService.getUserFineBalance(username);
        if (fineBalance > 0) {
            System.out.println("Cannot unregister: User has an outstanding fine of $" + fineBalance + ". Please pay all fines first.");
            return;
        }

        // Unregister the user
        if (userRepo.removeUser(username)) {
            System.out.println("User " + username + " unregistered successfully!");
        } else {
            System.out.println("Error unregistering user!");
        }
    }
}
