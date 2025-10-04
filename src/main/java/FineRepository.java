import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FineRepository {
    private final String FILE_PATH = "fines.csv";

    public FineRepository() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    
    public double getFineBalance(String username) {
        Map<String, Double> fines = loadFines();
        return fines.getOrDefault(username, 0.0);
    }

    
    public void updateFineBalance(String username, double newBalance) {
        Map<String, Double> fines = loadFines();
        fines.put(username, newBalance);
        saveFines(fines);
    }

   
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