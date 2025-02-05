package server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

/**
 * This is a service used to centralise long-polling.
 */
@Service
public class ListenerService {

    private final Map<Object, Consumer<String>> listeners = new ConcurrentHashMap<>();

    public void addListener(Object key, Consumer<String> listener) {
        listeners.put(key, listener);
    }

    public void removeListener(Object key) {
        listeners.remove(key);
    }

    public void notifyListeners(String content) {
        listeners.values().forEach(listener -> listener.accept(content));
    }
}
