import java.io.*;
import java.util.*;

public class UserRepository {
    private final String FILE_PATH = "users.csv";

    public UserRepository() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void addUser(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(user.getUsername() + "," + user.getPassword());
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public User findByUsername(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    return new User(parts[0], parts[1]);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updatePassword(String username, String newPassword) {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("temp_users.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    writer.write(username + "," + newPassword);
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
}
