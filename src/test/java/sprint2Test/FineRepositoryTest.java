package sprint2Test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import backend.repository.FineRepository;

import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class FineRepositoryTest {
    private FineRepository fineRepository;
    private File finesFile;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary fines.csv file
        finesFile = new File(tempDir, "fines.csv");
        fineRepository = new FineRepository(finesFile.getAbsolutePath());
        // Ensure the file is created
        finesFile.createNewFile();
    }

    @Test
    void testGetFineBalanceEmptyFile() {
        double balance = fineRepository.getFineBalance("user1");
        assertEquals(0.0, balance, "Should return 0.0 for non-existent user in empty file");
    }

    @Test
    void testGetFineBalanceExistingUser() throws IOException {
        // Write sample data to fines.csv
        try (FileWriter writer = new FileWriter(finesFile)) {
            writer.write("user1,10.5\n");
            writer.write("user2,20.0\n");
        }

        double balance = fineRepository.getFineBalance("user1");
        assertEquals(10.5, balance, "Should return correct fine balance for user1");
    }

    @Test
    void testGetFineBalanceNonExistentUser() throws IOException {
        // Write sample data to fines.csv
        try (FileWriter writer = new FileWriter(finesFile)) {
            writer.write("user1,10.5\n");
        }

        double balance = fineRepository.getFineBalance("user2");
        assertEquals(0.0, balance, "Should return 0.0 for non-existent user");
    }

    @Test
    void testUpdateFineBalanceNewUser() throws IOException {
        fineRepository.updateFineBalance("user1", 15.0);

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(finesFile))) {
            String line = br.readLine();
            assertEquals("user1,15.0", line);
            assertNull(br.readLine(), "File should contain only one line");
        }
    }

    @Test
    void testUpdateFineBalanceExistingUser() throws IOException {
        // Initialize file with a fine
        try (FileWriter writer = new FileWriter(finesFile)) {
            writer.write("user1,10.5\n");
            writer.write("user2,20.0\n");
        }

        fineRepository.updateFineBalance("user1", 25.0);

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(finesFile))) {
            String line1 = br.readLine();
            String line2 = br.readLine();
            assertTrue(line1.equals("user1,25.0") || line2.equals("user1,25.0"), "user1 balance should be updated to 25.0");
            assertTrue(line1.equals("user2,20.0") || line2.equals("user2,20.0"), "user2 balance should remain unchanged");
            assertNull(br.readLine(), "File should contain only two lines");
        }
    }

    @Test
    void testLoadFinesInvalidLine() throws IOException {
        // Write invalid data to fines.csv
        try (FileWriter writer = new FileWriter(finesFile)) {
            writer.write("user1,10.5\n");
            writer.write(",invalid\n"); // Invalid line: empty username
            writer.write("user2,not_a_number\n"); // Invalid line: non-numeric balance
        }

        double balance = fineRepository.getFineBalance("user1");
        assertEquals(10.5, balance, "Should load valid fine for user1");
        assertEquals(0.0, fineRepository.getFineBalance("user2"), "Should return 0.0 for user with invalid balance");
        assertEquals(0.0, fineRepository.getFineBalance("invalid"), "Should return 0.0 for invalid line");
    }

    @Test
    void testSaveFinesMultipleEntries() throws IOException {
        fineRepository.updateFineBalance("user1", 15.0);
        fineRepository.updateFineBalance("user2", 25.5);

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(finesFile))) {
            String line1 = br.readLine();
            String line2 = br.readLine();
            assertTrue(line1.equals("user1,15.0") || line2.equals("user1,15.0"), "user1 balance should be 15.0");
            assertTrue(line1.equals("user2,25.5") || line2.equals("user2,25.5"), "user2 balance should be 25.5");
            assertNull(br.readLine(), "File should contain only two lines");
        }
    }
}