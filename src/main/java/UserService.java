public class UserService {
    private UserRepository repo = new UserRepository();
    private User loggedInUser = null;

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

    public void createAccount(String username, String password) {
        if (repo.findByUsername(username) != null) {
            System.out.println("Username already exists!");
            return;
        }
        repo.addUser(new User(username, password));
        System.out.println("User account created successfully!");
    }

    public void resetPassword(String username, String newPassword) {
        if (repo.updatePassword(username, newPassword)) {
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Username not found!");
        }
    }
}
