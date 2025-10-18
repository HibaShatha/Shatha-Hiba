package sprint2Test;

import backend.model.User;
import backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private UserRepository userRepository;
    private File usersFile;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary users.csv file
        usersFile = new File(tempDir, "users.csv");
        userRepository = new UserRepository(usersFile.getAbsolutePath());
        // Ensure the file is created
        usersFile.createNewFile();
    }

    @Test
    void testAddUser() throws IOException {
        User user = new User("user1", "pass123", "user1@example.com", "1234567890");
        userRepository.addUser(user);

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line = br.readLine();
            assertEquals("user1,pass123,user1@example.com,1234567890", line);
            assertNull(br.readLine(), "File should contain only one line");
        }
    }

    @Test
    void testFindByUsernameExistingUser() throws IOException {
        // Write sample data to users.csv
        try (FileWriter writer = new FileWriter(usersFile)) {
            writer.write("user1,pass123,user1@example.com,1234567890\n");
            writer.write("user2,pass456,user2@example.com,0987654321\n");
        }

        User user = userRepository.findByUsername("user1");
        assertNotNull(user);
        assertEquals("user1", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("user1@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
    }

    @Test
    void testFindByUsernameNonExistentUser() {
        // Empty file
        User user = userRepository.findByUsername("user1");
        assertNull(user, "Should return null for non-existent user");
    }

    @Test
    void testUpdatePasswordSuccess() throws IOException {
        // Initialize file with a user
        try (FileWriter writer = new FileWriter(usersFile)) {
            writer.write("user1,pass123,user1@example.com,1234567890\n");
            writer.write("user2,pass456,user2@example.com,0987654321\n");
        }

        boolean result = userRepository.updatePassword("user1", "newPass789");
        assertTrue(result, "Update should return true for existing user");

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line1 = br.readLine();
            String line2 = br.readLine();
            assertTrue(line1.equals("user1,newPass789,user1@example.com,1234567890") ||
                       line2.equals("user1,newPass789,user1@example.com,1234567890"),
                       "user1 password should be updated");
            assertTrue(line1.equals("user2,pass456,user2@example.com,0987654321") ||
                       line2.equals("user2,pass456,user2@example.com,0987654321"),
                       "user2 should remain unchanged");
            assertNull(br.readLine(), "File should contain only two lines");
        }
    }

    @Test
    void testUpdatePasswordNonExistentUser() throws IOException {
        // Initialize file with a user
        try (FileWriter writer = new FileWriter(usersFile)) {
            writer.write("user1,pass123,user1@example.com,1234567890\n");
        }

        boolean result = userRepository.updatePassword("user2", "newPass789");
        assertFalse(result, "Update should return false for non-existent user");

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line = br.readLine();
            assertEquals("user1,pass123,user1@example.com,1234567890", line);
            assertNull(br.readLine(), "File should contain only one line");
        }
    }

    @Test
    void testRemoveUserSuccess() throws IOException {
        // Initialize file with users
        try (FileWriter writer = new FileWriter(usersFile)) {
            writer.write("user1,pass123,user1@example.com,1234567890\n");
            writer.write("user2,pass456,user2@example.com,0987654321\n");
        }

        boolean result = userRepository.removeUser("user1");
        assertTrue(result, "Remove should return true for existing user");

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line = br.readLine();
            assertEquals("user2,pass456,user2@example.com,0987654321", line);
            assertNull(br.readLine(), "File should contain only one line");
        }
    }

    @Test
    void testRemoveUserNonExistentUser() throws IOException {
        // Initialize file with a user
        try (FileWriter writer = new FileWriter(usersFile)) {
            writer.write("user1,pass123,user1@example.com,1234567890\n");
        }

        boolean result = userRepository.removeUser("user2");
        assertFalse(result, "Remove should return false for non-existent user");

        // Read the file and verify content
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line = br.readLine();
            assertEquals("user1,pass123,user1@example.com,1234567890", line);
            assertNull(br.readLine(), "File should contain only one line");
        }
    }
}