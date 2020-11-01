package io.msengine.common.util.event;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <E> Object onEvent type.
 */
public class ObjectEventManager<E extends ObjectEvent> {

    private static final Logger LOGGER = Logger.getLogger("mse.event");
    private static final Class<?> EVENT_SUPER_CLASS = ObjectEvent.class.getSuperclass();

    private final HashMap<Class<?>, List<ObjectEventListener<?>>> listeners = new HashMap<>();

    public <T extends E> void addEventListener(Class<T> clazz, ObjectEventListener<T> listener) {
        this.listeners.computeIfAbsent(clazz, cl -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public <T extends E> void removeEventListener(Class<T> clazz, ObjectEventListener<T> listener) {
        List<ObjectEventListener<?>> group = this.listeners.get(clazz);
        if (group != null) {
            group.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends E> void fireEvent(T event) {

        for (Class<?> clazz = event.getClass(); clazz != EVENT_SUPER_CLASS; clazz = clazz.getSuperclass()) {
            List<ObjectEventListener<?>> group = this.listeners.get(clazz);
            if (group != null) {
                group.forEach(l -> {
                    try {
                        ((ObjectEventListener<? super T>) l).onEvent(event);
                    } catch (RuntimeException e) {
                        LOGGER.log(Level.SEVERE, "Error when firing event.", e);
                    }
                });
            }
        }

    }
    
}
