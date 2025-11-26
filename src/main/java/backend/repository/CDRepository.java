package backend.repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import backend.model.CD;

/**
 * Repository class for managing CD data.
 * <p>
 * Provides methods to add, retrieve, and update CDs stored in a CSV file.
 * Automatically creates the file if it does not exist.
 * </p>
 * <p>
 * Only important methods are documented to keep it clean and professional.
 * </p>
 * 
 * @author Hiba_ibraheem
 * @version 1.0.0
 */
public class CDRepository {

    /** Path to the CSV file storing CD data */
    private final String FILE_PATH;

    /** Default constructor using "cds.csv" */
    public CDRepository() {
        this("files/cds.csv");
    }

    /**
     * Constructor with custom file path.
     *
     * @param filePath path to the CSV file
     */
    public CDRepository(String filePath) {
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

    /** Adds a new CD to the CSV file */
    public void addCD(CD cd) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String dueDateStr = cd.getDueDate() != null ? cd.getDueDate().toString() : "";
            String borrower = cd.getBorrowerUsername() != null ? cd.getBorrowerUsername() : "";
            bw.write(cd.getTitle() + "," + cd.getAuthor() + "," + cd.isBorrowed() + "," + dueDateStr + "," + borrower);
            bw.newLine();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Retrieves all CDs from the CSV file.
     * <p>
     * Parses each line into a CD object, including borrowed status, due date, and borrower.
     * </p>
     *
     * @return list of all CDs
     */
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

    /**
     * Updates a CD's information in the CSV file.
     * <p>
     * Uses a temporary file to safely replace the original file.
     * </p>
     *
     * @param cd the CD object with updated information
     * @return true if the CD was found and updated; false otherwise
     */
    public boolean updateCD(CD cd) {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("files/temp_cds.csv");

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
