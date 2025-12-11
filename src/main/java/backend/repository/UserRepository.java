package backend.repository;

import java.io.*;
import backend.model.User;

/**
 * Repository class for managing users.
 * <p>
 * Provides methods to add, find, update password, and remove users stored in a CSV file.
 * Automatically creates the file if it does not exist.
 * </p>
 * <p>
 * Version 1.2.0 - Updated to safely handle CSV parsing, password updates, and user removal.
 * </p>
 * 
 * @author Hiba_ibraheem
 * @version 1.2.0
 */
public class UserRepository {

    /** Path to the CSV file storing user data */
    private final String FILE_PATH;

    /** Default constructor using "users.csv" */
    public UserRepository() {
        this("files/users.csv");
    }

    /**
     * Constructor with custom file path.
     *
     * @param filePath path to the CSV file
     */
    public UserRepository(String filePath) {
        this.FILE_PATH = filePath;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    /** Returns the file path used by this repository */
    public String getFilePath() {
        return FILE_PATH;
    }

    /** Adds a new user to the CSV file */
    public void addUser(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(user.getUsername() + "," + user.getPassword() + "," + user.getEmail() + "," + user.getPhoneNumber());
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return the User object if found; null otherwise
     */
    public User findByUsername(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4 && parts[0].equals(username)) {
                    String email = parts.length > 2 ? parts[2] : "";
                    String phoneNumber = parts.length > 3 ? parts[3] : "";
                    return new User(parts[0], parts[1], email, phoneNumber);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Updates the password for a specific user.
     *
     * @param username    the username to update
     * @param newPassword the new password
     * @return true if the user was found and updated; false otherwise
     */
    public boolean updatePassword(String username, String newPassword) {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("files/temp_users.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                String email = parts.length > 2 ? parts[2] : "";
                String phoneNumber = parts.length > 3 ? parts[3] : "";
                if (parts[0].equals(username)) {
                    writer.write(username + "," + newPassword + "," + email + "," + phoneNumber);
                    found = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            reader.close();
            writer.close();

            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.out.println("Error updating password file");
                return false;
            }

            return found;
        } catch (IOException e) { e.printStackTrace(); return false; }
    }

    /**
     * Removes a user from the CSV file.
     *
     * @param username the username to remove
     * @return true if the user was found and removed; false otherwise
     */
    public boolean removeUser(String username) {
        File inputFile = new File(FILE_PATH);
        File tempFile = new File("files/temp_users.csv");
        boolean found = false;

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (!parts[0].equals(username)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    found = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // بعد الانتهاء من الكتابة، حذف الملف القديم وإعادة تسمية الملف المؤقت
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.out.println("Error deleting user from file");
            return false;
        }

        return found;
    }

}
