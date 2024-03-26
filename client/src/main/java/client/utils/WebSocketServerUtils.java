package client.utils;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * The Server Utils for Web Socket connection.
 */
public class WebSocketServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    private StompSession session = connect("ws://localhost:8080/websocket");

    private StompSession connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {}).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    /**
     * This registers the client for messages.
     *
     * @param dest the destination
     * @param type the class
     * @param consumer the consumer
     * @param <T> the type
     */
    public <T> void registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    /**
     * A method used to send web socket messages.
     *
     * @param destination the destination of the message
     * @param message the message
     */
    public void sendWebSocketMessage(String destination, String message) {
        if (session != null && session.isConnected()) {
            session.send(destination, message);
        }
    }
}
