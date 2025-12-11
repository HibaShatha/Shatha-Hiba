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
    private final String FILE_PATH;

    public UserRepository() {
        this("files/users.csv");
    }

    public UserRepository(String filePath) {
        this.FILE_PATH = filePath;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public String getFilePath() {
        return FILE_PATH;
    }

    public void addUser(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(formatUser(user));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User findByUsername(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4 && parts[0].equals(username)) {
                    return new User(parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(String username, String newPassword) {
        return modifyFile(username, newPassword, false);
    }

    public boolean removeUser(String username) {
        return modifyFile(username, null, true);
    }

    /**
     * General method to update password or remove user.
     *
     * @param username    username to find
     * @param newPassword new password if updating, null if removing
     * @param remove      true if removing user, false if updating
     * @return true if operation succeeded
     */
    private boolean modifyFile(String username, String newPassword, boolean remove) {
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
                if (parts[0].equals(username)) {
                    found = true;
                    if (!remove) {
                        // update password
                        writer.write(parts[0] + "," + newPassword + "," + parts[2] + "," + parts[3]);
                        writer.newLine();
                    }
                    // if remove, skip writing line
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.out.println(remove ? "Error deleting user from file" : "Error updating password file");
            return false;
        }

        return found;
    }

    /** Helper to format a user as CSV line */
    private String formatUser(User user) {
        return String.join(",", user.getUsername(), user.getPassword(), user.getEmail(), user.getPhoneNumber());
    }
}
