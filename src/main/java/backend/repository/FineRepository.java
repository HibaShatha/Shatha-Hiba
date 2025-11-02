package backend.repository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository class for managing fines of users.
 * <p>
 * Provides methods to get and update fine balances stored in a CSV file.
 * Automatically creates the file if it does not exist.
 * </p>
 * <p>
 * Version 1.1.0 - Updated CSV parsing to handle invalid lines and add error messages.
 * </p>
 * 
 * @author Hiba_ibraheem
 * @version 1.1
 */
public class FineRepository {

    /** Path to the CSV file storing user fines */
    private final String FILE_PATH;

    /** Default constructor using "fines.csv" */
    public FineRepository() {
        this("fines.csv");
    }

    /**
     * Constructor with custom file path.
     *
     * @param filePath path to the CSV file
     */
    public FineRepository(String filePath) {
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

    /**
     * Retrieves the fine balance for a specific user.
     *
     * @param username the username to check
     * @return the fine balance; 0.0 if the user has no fines
     */
    public double getFineBalance(String username) {
        Map<String, Double> fines = loadFines();
        return fines.getOrDefault(username, 0.0);
    }

    /**
     * Updates the fine balance of a specific user.
     *
     * @param username   the username to update
     * @param newBalance the new fine balance
     */
    public void updateFineBalance(String username, double newBalance) {
        Map<String, Double> fines = loadFines();
        fines.put(username, newBalance);
        saveFines(fines);
    }

    /**
     * Loads fines from the CSV file into a Map.
     * <p>
     * Handles invalid lines and parses balances safely.
     * </p>
     *
     * @return a Map with username as key and fine balance as value
     */
    private Map<String, Double> loadFines() {
        Map<String, Double> fines = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 2 || parts[0].isEmpty()) {
                    System.out.println("Invalid line in fines.csv: " + line);
                    continue;
                }
                try {
                    fines.put(parts[0], Double.parseDouble(parts[1]));
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing fine balance in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fines;
    }

    /**
     * Saves fines from a Map into the CSV file.
     *
     * @param fines Map containing usernames and their fine balances
     */
    private void saveFines(Map<String, Double> fines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, Double> entry : fines.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
