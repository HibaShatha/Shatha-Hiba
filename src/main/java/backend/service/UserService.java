package backend.service;

import backend.model.User;
import backend.repository.UserRepository;

public class UserService {
    public UserRepository repo;
    private User loggedInUser = null;

    public UserService() {
        this(new UserRepository());
    }

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public boolean login(String username, String password) {
        User user = repo.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user;
            System.out.println("User login successful!");
            return true;
        } else {
            System.out.println("Invalid credentials!");
            return false;
        }
    }

    public void logout() {
        loggedInUser = null;
        System.out.println("Logged out successfully!");
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public String getLoggedInUsername() {
        return loggedInUser != null ? loggedInUser.getUsername() : null;
    }

    public String getLoggedInUserEmail() {
        return loggedInUser != null ? loggedInUser.getEmail() : null;
    }

    public void createAccount(String username, String password, String email, String phoneNumber) {
        if (repo.findByUsername(username) != null) {
            System.out.println("Username already exists!");
            return;
        }
        repo.addUser(new User(username, password, email, phoneNumber));
        System.out.println("User account created successfully!");
    }

    public String getEmailByUsername(String username) {
        User user = repo.findByUsername(username);
        return user != null ? user.getEmail() : null;
    }

    public void resetPassword(String username, String newPassword) {
        if (repo.updatePassword(username, newPassword)) {
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Username not found!");
        }
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }
}