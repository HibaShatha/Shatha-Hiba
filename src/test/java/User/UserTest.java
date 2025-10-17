package User;

import backend.model.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("john_doe", "pass123", "john@example.com", "1234567890");
    }

    @Test
    @DisplayName("Test getters")
    void testGetters() {
        assertEquals("john_doe", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
    }
}
