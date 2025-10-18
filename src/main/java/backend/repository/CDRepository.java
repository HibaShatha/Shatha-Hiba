package backend.repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import backend.model.CD;

public class CDRepository {
    private final String FILE_PATH;

    public CDRepository() {
        this("cds.csv");
    }

    public CDRepository(String filePath) {
        this.FILE_PATH = filePath;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public String getFilePath() {
        return FILE_PATH;
    }

    public void addCD(CD cd) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String dueDateStr = cd.getDueDate() != null ? cd.getDueDate().toString() : "";
            String borrower = cd.getBorrowerUsername() != null ? cd.getBorrowerUsername() : "";
            bw.write(cd.getTitle() + "," + cd.getAuthor() + "," + cd.isBorrowed() + "," + dueDateStr + "," + borrower);
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<CD> getAllCDs() {
        List<CD> cds = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue;

                CD cd = new CD(parts[0], parts[1]);
                cd.borrowed = Boolean.parseBoolean(parts[2]);
                if (parts.length > 3 && !parts[3].isEmpty()) {
                    try { cd.setDueDate(LocalDate.parse(parts[3])); } catch (Exception e) { cd.setDueDate(null); }
                }
                if (parts.length > 4 && !parts[4].isEmpty()) cd.borrowerUsername = parts[4];
                cds.add(cd);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return cds;
    }

    public boolean updateCD(CD cd) {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("temp_cds.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2 && parts[0].equals(cd.getTitle()) && parts[1].equals(cd.getAuthor())) {
                    String dueDateStr = cd.getDueDate() != null ? cd.getDueDate().toString() : "";
                    String borrower = cd.getBorrowerUsername() != null ? cd.getBorrowerUsername() : "";
                    writer.write(cd.getTitle() + "," + cd.getAuthor() + "," + cd.isBorrowed() + "," + dueDateStr + "," + borrower);
                    found = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            reader.close();
            writer.close();

            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.out.println("Error updating CD file");
                return false;
            }

            return found;
        } catch (IOException e) { e.printStackTrace(); return false; }
    }
}