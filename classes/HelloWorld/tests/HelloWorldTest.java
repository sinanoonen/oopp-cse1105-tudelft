import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class HelloWorldTest {

    @Test
    void greeting() {
        assertEquals("Hello World! 🌍", new HelloWorld().greeting());
    }
}