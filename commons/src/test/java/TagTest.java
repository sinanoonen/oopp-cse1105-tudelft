import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.Color;
import org.junit.jupiter.api.Test;

/**
 * For testing the Tag class.
 */
public class TagTest {

    @Test
    public void testConstructorAndGetters() {
        String name = "TestTag";
        Color color = Color.RED;
        Tag tag = new Tag(name, color);
        assertEquals(name, tag.getName());
        assertEquals(color, tag.getColor());
    }

    @Test
    public void testSetters() {
        Tag tag = new Tag("InitialTag", Color.BLUE);
        tag.setName("UpdatedTag");
        assertEquals("UpdatedTag", tag.getName());
        tag.setColor(Color.GREEN);
        assertEquals(Color.GREEN, tag.getColor());
    }

    @Test
    public void testToString() {
        Tag tag = new Tag("TestTag", Color.RED);
        String expectedString = "Tag{name='TestTag', color=java.awt.Color[r=255,g=0,b=0]}";
        assertEquals(expectedString, tag.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        Tag tag1 = new Tag("TestTag", Color.RED);
        Tag tag2 = new Tag("TestTag", Color.RED);
        Tag tag3 = new Tag("DifferentTag", Color.BLUE);
        Tag tag4 = new Tag("TestTag", Color.YELLOW);
        Tag tag5 = new Tag("DifferentTag", Color.RED);
        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);
        assertNotEquals(tag1, tag4);
        assertNotEquals(tag1, tag5);
        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertNotEquals(tag1.hashCode(), tag3.hashCode());
        assertNotEquals(tag1.hashCode(), tag4.hashCode());
        assertNotEquals(tag1.hashCode(), tag5.hashCode());
    }

    @Test
    public void testEqualsMethodWithDifferentClass() {
        Tag tag = new Tag("TestTag", Color.RED);
        assertNotEquals("TestTag", tag);
    }

    @Test
    public void testEqualsMethodWithNullColor() {
        Tag tag1 = new Tag("TestTag", null);
        Tag tag2 = new Tag("TestTag", null);
        Tag tag3 = new Tag("TestTag", Color.RED);
        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);
    }

    @Test
    public void testEqualsMethodWithNullName() {
        Tag tag1 = new Tag(null, Color.RED);
        Tag tag2 = new Tag(null, Color.RED);
        Tag tag3 = new Tag("Ciao", Color.RED);
        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);
    }
}
