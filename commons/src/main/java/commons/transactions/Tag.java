package commons.transactions;

import jakarta.persistence.*;

import java.awt.Color;
import java.util.Objects;

/**
 * The tag class.
 */
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private int color;

    @SuppressWarnings("unused")
    protected Tag() {

    }

    /**
     * Constructor for the tag.
     *
     * @param name the name of the tag
     * @param color the color of the tag
     */
    public Tag(String name, Color color) {
        this.name = name;
        int nullMagenta = new Color(247, 0, 213).getRGB();
        this.color = color == null ? nullMagenta : color.getRGB();
    }

    /**
     * Getter for the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name.
     *
     * @param name sets the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the color.
     *
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * Setter for the color.
     *
     * @param color sets the color
     */
    public void setColor(int color) {
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * A string representation of the tag.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "commons.transactions.Tag{"
            + "name='" + name + '\''
            + ", color=" + color
            + '}';
    }

    /**
     * The equals method.
     *
     * @param object the object to be compared to
     * @return true iff this and the object are equal
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!object.getClass().equals(getClass())) {
            return false;
        }

        Tag tag = (Tag) object;

        return Objects.equals(name, tag.name) && Objects.equals(color, tag.color);
    }

    /**
     * Generates a hashCode for the tag.
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
