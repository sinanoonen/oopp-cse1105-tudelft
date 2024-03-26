package commons;

import java.util.Objects;

/**
 * This class is used to send web socket messages with a specific format.
 */
public class WebSocketMessage {
    private String content;

    public WebSocketMessage() {}

    public WebSocketMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof WebSocketMessage that)) {
            return false;
        }
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
