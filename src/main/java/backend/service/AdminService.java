package backend.service;
import java.util.Scanner;

import backend.model.Admin;
import backend.repository.AdminRepository;

public class AdminService {
    private AdminRepository repo = new AdminRepository();
    private Admin loggedInAdmin = null;

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
}