package backend.model;
/**
 * Represents a user in the system.
 * <p>
 * Stores basic user information such as username, password, email, and phone number.
 * This class is primarily used for authentication and user management.
 * </p>
 * 
 * <p>
 * All fields are private and accessed via getters.
 * </p>
 * 
 * @author Shatha_Dweikat
 * @version 1.0.0
 */
public class User {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;

    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
}