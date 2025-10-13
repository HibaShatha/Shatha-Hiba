package backend.model;

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