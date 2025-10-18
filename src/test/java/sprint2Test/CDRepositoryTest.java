package sprint2Test;

import backend.model.CD;
import backend.repository.CDRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CDRepositoryTest {
    private CDRepository cdRepository;
    private File cdsFile;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary cds.csv file
        cdsFile = new File(tempDir, "cds.csv");
        cdRepository = new CDRepository(cdsFile.getAbsolutePath());
        // Ensure the file is created
        cdsFile.createNewFile();
    }

    @Test
    void testAddCDNotBorrowed() throws IOException {
        CD cd = new CD("Test CD", "Artist Name");
        cdRepository.addCD(cd);

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(cdsFile))) {
            String line = br.readLine();
            assertEquals("Test CD,Artist Name,false,,", line);
        }
    }

    @Test
    void testAddCDBorrowed() throws IOException {
        CD cd = new CD("Test CD", "Artist Name");
        cd.borrow("user1");
        cd.setDueDate(LocalDate.of(2025, 10, 1));
        cdRepository.addCD(cd);

        try (BufferedReader br = new BufferedReader(new FileReader(cdsFile))) {
            String line = br.readLine();
            assertEquals("Test CD,Artist Name,true,2025-10-01,user1", line);
        }
    }

    @Test
    void testGetAllCDsEmptyFile() {
        List<CD> cds = cdRepository.getAllCDs();
        assertTrue(cds.isEmpty(), "Should return empty list for empty file");
    }

    @Test
    void testGetAllCDsMultipleCDs() throws IOException {
        // Write sample data to cds.csv
        try (FileWriter writer = new FileWriter(cdsFile)) {
            writer.write("CD1,Artist1,true,2025-10-01,user1\n");
            writer.write("CD2,Artist2,false,,\n");
        }

        List<CD> cds = cdRepository.getAllCDs();
        assertEquals(2, cds.size());
        assertEquals("CD1", cds.get(0).getTitle());
        assertEquals("Artist1", cds.get(0).getAuthor());
        assertTrue(cds.get(0).isBorrowed());
        assertEquals(LocalDate.of(2025, 10, 1), cds.get(0).getDueDate());
        assertEquals("user1", cds.get(0).getBorrowerUsername());
        assertEquals("CD2", cds.get(1).getTitle());
        assertEquals("Artist2", cds.get(1).getAuthor());
        assertFalse(cds.get(1).isBorrowed());
        assertNull(cds.get(1).getDueDate());
        assertNull(cds.get(1).getBorrowerUsername());
    }

    @Test
    void testUpdateCDSuccess() throws IOException {
        // Initialize file with a CD
        try (FileWriter writer = new FileWriter(cdsFile)) {
            writer.write("Music Album,Artist1,false,,\n");
        }

        CD updatedCD = new CD("Music Album", "Artist1");
        updatedCD.borrow("user1");
        updatedCD.setDueDate(LocalDate.of(2025, 10, 1));
        boolean result = cdRepository.updateCD(updatedCD);

        assertTrue(result, "Update should return true for existing CD");
        try (BufferedReader br = new BufferedReader(new FileReader(cdsFile))) {
            String line = br.readLine();
            assertEquals("Music Album,Artist1,true,2025-10-01,user1", line);
        }
    }

    @Test
    void testUpdateCDNotFound() throws IOException {
        // Initialize file with a CD
        try (FileWriter writer = new FileWriter(cdsFile)) {
            writer.write("Music Album,Artist1,false,,\n");
        }

        CD updatedCD = new CD("Nonexistent CD", "Artist2");
        boolean result = cdRepository.updateCD(updatedCD);

        assertFalse(result, "Update should return false for non-existing CD");
        try (BufferedReader br = new BufferedReader(new FileReader(cdsFile))) {
            String line = br.readLine();
            assertEquals("Music Album,Artist1,false,,", line);
        }
    }
}