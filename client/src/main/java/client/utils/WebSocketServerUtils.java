package client.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

    private static StompSession session = connect("ws://localhost:8080/websocket");
    private static String ip = "localhost";
    private static String port = "8080";
    private static String SERVER = "ws://localhost:8080/websocket";
    private Map<String, StompSession.Subscription> subscriptions = new HashMap<>();

    /**
     * This should be used for changing the server.
     *
     * @param ip the ip of the server
     * @param port the port of the server
     */
    public static void setSession(String ip, String port) {
        WebSocketServerUtils.ip = ip;
        WebSocketServerUtils.port = port;
        WebSocketServerUtils.SERVER = "ws://" + ip + ":" + port + "/websocket";
        session = connect(SERVER);
    }

    private static StompSession connect(String url) {
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
        StompSession.Subscription subscription = session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });

        subscriptions.put(dest, subscription);
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

    /**
     * This unregisters the client for messages.
     *
     * @param dest the destination of the message
     */
    public void unregisterFromMessages(String dest) {
        StompSession.Subscription subscription = subscriptions.get(dest);
        if (subscription != null) {
            subscription.unsubscribe();
            subscriptions.remove(dest);
        }
    }

    /**
     * This removes all subscriptions for messages.
     */
    public void unsubscribeAll() {
        Iterator<StompSession.Subscription> iterator = subscriptions.values().stream().iterator();
        while (iterator.hasNext()) {
            StompSession.Subscription subscription = iterator.next();
            subscription.unsubscribe();
            subscriptions.remove(subscription);
        }
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        WebSocketServerUtils.ip = ip;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        WebSocketServerUtils.port = port;
    }

    public static String getSERVER() {
        return SERVER;
    }

    public static void setSERVER(String server) {
        WebSocketServerUtils.SERVER = server;
    }
}
