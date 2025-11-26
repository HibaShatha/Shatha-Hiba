package backend.repository;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import backend.model.Admin;

/**
 * Repository class for managing Admin data.
 * <p>
 * Provides methods to add, find, and update Admin records stored in a CSV file.
 * </p>
 * <p>
 * Handles file creation automatically if the CSV file does not exist.
 * </p>
 * 
 * @author Shatha_Dweikat
 * @version 1.0.0
 */
public class AdminRepository {

    /** Path to the CSV file storing admin data */
    public final String FILE_PATH;

    /** Default constructor using "admins.csv" as the file path */
    public AdminRepository() {
        this("files/admins.csv");
    }

    /**
     * Constructor with custom file path.
     * <p>
     * Creates the file if it does not exist.
     * </p>
     *
     * @param filePath the path to the CSV file
     */
    public AdminRepository(String filePath) {
        this.FILE_PATH = filePath;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    /** Adds a new admin to the CSV file */
    public void addAdmin(Admin admin) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(admin.getUsername() + "," + admin.getPassword());
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Finds an admin by username.
     *
     * @param username the username to search for
     * @return the Admin object if found; null otherwise
     */
    public Admin findByUsername(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    return new Admin(parts[0], parts[1]);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Updates the password of an existing admin.
     * <p>
     * Uses a temporary file to safely replace the original CSV.
     * </p>
     *
     * @param username    the username of the admin
     * @param newPassword the new password to set
     * @return true if the update was successful; false if the admin was not found
     */
    public boolean updatePassword(String username, String newPassword) {
        Path inputPath = Paths.get(FILE_PATH);
        Path dir = inputPath.getParent();
        if (dir == null) dir = Paths.get(".");

        try {
            Path tempPath = Files.createTempFile(dir, "temp_admins", ".csv");
            boolean found = false;

            try (BufferedReader reader = Files.newBufferedReader(inputPath);
                 BufferedWriter writer = Files.newBufferedWriter(tempPath)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", 2);
                    if (parts.length > 0 && parts[0].equals(username)) {
                        writer.write(username + "," + newPassword);
                        found = true;
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                }
            }

            if (!found) {
                Files.deleteIfExists(tempPath);
                return false;
            }

            Files.move(tempPath, inputPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
