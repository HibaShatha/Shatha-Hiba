package backend.repository;

import java.io.*;
import java.util.*;
import java.nio.file.*;

import backend.model.Admin;

public class AdminRepository {
    public final String FILE_PATH ;


    public AdminRepository() {
        this("admins.csv"); // المسار الافتراضي
    }

    public AdminRepository(String filePath) {
        this.FILE_PATH = filePath;

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
        Path inputPath = Paths.get(FILE_PATH);
        // أنشئ ملف مؤقت في نفس مجلد الملف الأصلي
        Path dir = inputPath.getParent();
        if (dir == null) {
            dir = Paths.get("."); // حالة المسار بدون مجلد
        }

        try {
            Path tempPath = Files.createTempFile(dir, "temp_admins", ".csv");

            boolean found = false;

            try (BufferedReader reader = Files.newBufferedReader(inputPath);
                 BufferedWriter writer = Files.newBufferedWriter(tempPath)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", 2); // امنع مشاكل لو كلمة السر فيها فاصلة
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
                // المستخدم مش موجود — نحذف الملف المؤقت ونترك الملف الأصلي كما هو
                Files.deleteIfExists(tempPath);
                return false;
            }

            // استبدال الملف الأصلي بالمؤقت (آمن، ويستبدل لو موجود)
            Files.move(tempPath, inputPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
