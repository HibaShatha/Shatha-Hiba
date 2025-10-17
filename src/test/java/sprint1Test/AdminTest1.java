package sprint1Test;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import backend.model.Admin;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ Comprehensive Unit Tests for the Admin class.
 * Tests normal, edge, and error-like scenarios.
 */
class AdminTest1 {

    @Test
    @DisplayName("✅ Constructor sets username and password correctly")
    void testConstructorAndGetters() {
        Admin admin = new Admin("shatha", "1234");
        assertEquals("shatha", admin.getUsername());
        assertEquals("1234", admin.getPassword());
    }

    @Test
    @DisplayName("✅ Multiple Admin objects are independent")
    void testFieldsIndependence() {
        Admin admin1 = new Admin("user1", "pass1");
        Admin admin2 = new Admin("user2", "pass2");

        assertNotEquals(admin1.getUsername(), admin2.getUsername());
        assertNotEquals(admin1.getPassword(), admin2.getPassword());
    }

    @Test
    @DisplayName("⚠️ Constructor should handle empty strings")
    void testEmptyStrings() {
        Admin admin = new Admin("", "");
        assertEquals("", admin.getUsername());
        assertEquals("", admin.getPassword());
    }

    @Test
    @DisplayName("🚫 Constructor should allow null values but store them correctly")
    void testNullValues() {
        Admin admin = new Admin(null, null);
        assertNull(admin.getUsername(), "Username can be null");
        assertNull(admin.getPassword(), "Password can be null");
    }

    @Test
    @DisplayName("🔁 Two admins with same username & password should be logically equal (if equals added later)")
    void testEqualityLogic() {
        Admin a1 = new Admin("same", "pwd");
        Admin a2 = new Admin("same", "pwd");

        // since equals() not overridden, they should not be equal as objects
        assertNotEquals(a1, a2, "Different instances should not be equal (default behavior)");
        // but their data should match
        assertEquals(a1.getUsername(), a2.getUsername());
        assertEquals(a1.getPassword(), a2.getPassword());
    }

    @Test
    @DisplayName("🔍 Username is case-sensitive (e.g., 'Admin' != 'admin')")
    void testCaseSensitivity() {
        Admin admin1 = new Admin("Admin", "123");
        Admin admin2 = new Admin("admin", "123");

        assertNotEquals(admin1.getUsername(), admin2.getUsername());
    }

    @Test
    @DisplayName("🧱 Stress test: create many Admin instances without issue")
    void testMultipleInstances() {
        for (int i = 0; i < 10000; i++) {
            Admin admin = new Admin("user" + i, "pass" + i);
            assertNotNull(admin);
            assertTrue(admin.getUsername().startsWith("user"));
        }
    }
}
