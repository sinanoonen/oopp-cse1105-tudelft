import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class UserTest {
    User testUser;

    @BeforeEach
    void setupForTests() {
        testUser = new User("testName", "testEmail", "testIban", "testBic");
    }

    @Test
    void constructor() {
        assertNotNull(testUser);
        assertEquals("testName", testUser.getName());
        assertEquals("testEmail", testUser.getEmail());
        assertEquals("testIban", testUser.getIban());
        assertEquals("testBic", testUser.getBic());
    }

    @Test
    void getName() {
        assertEquals("testName", testUser.getName());
        assertNotEquals("testName2", testUser.getName());
    }

    @Test
    void getEmail() {
        assertEquals("testEmail", testUser.getEmail());
        assertNotEquals("testEmail2", testUser.getEmail());
    }

    @Test
    void getIban() {
        assertEquals("testIban", testUser.getIban());
        assertNotEquals("testIban2", testUser.getIban());
    }

    @Test
    void getBic() {
        assertEquals("testBic", testUser.getBic());
        assertNotEquals("testBic2", testUser.getBic());
    }

    @Test
    void setName() {
        testUser.setName("testName2");
        assertEquals("testName2", testUser.getName());
        assertNotEquals("testName", testUser.getName());
    }

    @Test
    void setEmail() {
        testUser.setEmail("testEmail2");
        assertEquals("testEmail2", testUser.getEmail());
        assertNotEquals("testEmail", testUser.getEmail());
    }

    @Test
    void setIban() {
        testUser.setIban("testIban2");
        assertEquals("testIban2", testUser.getIban());
        assertNotEquals("testIban", testUser.getIban());
    }

    @Test
    void setBic() {
        testUser.setBic("testBic2");
        assertEquals("testBic2", testUser.getBic());
        assertNotEquals("testBic", testUser.getBic());
    }

    @Test
    void testToString() {
        assertEquals("User{name='testName', email='testEmail', IBAN='testIban', BIC='testBic'}",
                testUser.toString());
    }

    @Test
    void testEquals() {
        User testUser2 = new User("testName", "testEmail", "testIban", "testBic");
        User testUser3 = new User("testName2", "testEmail2", "testIban2", "testBic2");
        User testUser4 = new User("testName", "testEmail2", "testIban2", "testBic2");
        User testUser5 = new User("testName", "testEmail", "testIban2", "testBic2");
        User testUser6 = new User("testName", "testEmail", "testIban", "testBic2");

        assertEquals(testUser, testUser2);
        assertEquals(testUser, testUser);
        assertNotEquals(testUser, testUser3);
        assertNotEquals(testUser, testUser4);
        assertNotEquals(testUser, testUser5);
        assertNotEquals(testUser, testUser6);
    }

    @Test
    void testHashCode() {
        User testUser2 = new User("testName", "testEmail", "testIban", "testBic");
        User testUser3 = new User("testName2", "testEmail2", "testIban2", "testBic2");
        User testUser4 = new User("testName", "testEmail2", "testIban2", "testBic2");
        User testUser5 = new User("testName", "testEmail", "testIban2", "testBic2");
        User testUser6 = new User("testName", "testEmail", "testIban", "testBic2");

        assertEquals(testUser.hashCode(), testUser2.hashCode());
        assertNotEquals(testUser.hashCode(), testUser3.hashCode());
        assertNotEquals(testUser.hashCode(), testUser4.hashCode());
        assertNotEquals(testUser.hashCode(), testUser5.hashCode());
        assertNotEquals(testUser.hashCode(), testUser6.hashCode());
    }

    @Test
    void testConstructorWithNullParameters() {
        User nullUser = new User(null, null, null, null);
        assertNotNull(nullUser);
        assertNull(nullUser.getName());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getIban());
        assertNull(nullUser.getBic());
    }

    @Test
    void testEqualsAndHashCodeWithDifferentType() {
        assertNotEquals(testUser, "not a user object");
    }

    @Test
    void testEqualsAndHashCodeWithNull() {
        assertNotEquals(testUser, null);
    }

    @Test
    void testUniqueConstraints() {
        User duplicateUser = new User("duplicateName", "testEmail",
                "testIban", "uniqueBic");
        assertNotEquals(testUser, duplicateUser);
    }
}