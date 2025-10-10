package backend.repository;

import java.io.*;
import java.util.*;

import backend.model.Admin;

public class AdminRepository {
    private final String FILE_PATH = "admins.csv";

    public AdminRepository() {
        // إذا الملف مش موجود نعمله
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void addAdmin(Admin admin) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(admin.getUsername() + "," + admin.getPassword());
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

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

    public boolean updatePassword(String username, String newPassword) {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("temp.csv");

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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
