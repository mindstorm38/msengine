package io.msengine.common.util.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class MethodEventManager {

	private static final Logger LOGGER = Logger.getLogger("msengine.event");

	private final HashMap<Class<?>, CopyOnWriteArrayList<?>> eventListeners = new HashMap<>();
	private final HashSet<Class<?>> allowedClass = new HashSet<>();
	/*private final HashSet<Class<?>> firingListeners = new HashSet<>();
	private final HashMap<Class<?>, List<?>> toAddListeners = new HashMap<>();
	private final HashMap<Class<?>, List<?>> toRemoveListeners = new HashMap<>();*/
	
	public MethodEventManager(Class<?>...allowedClasses) {
		Collections.addAll(this.allowedClass, allowedClasses);
	}

	// ALLOWED CLASSES //

	public void addAllowedClass(Class<?> clazz) {
		this.allowedClass.add(clazz);
	}
	
	public void removeAllowedClass(Class<?> clazz) {
		if (this.allowedClass.remove(clazz)) {
			this.eventListeners.remove(clazz);
		}
	}
	
	public boolean isClassAllowed(Class<?> clazz) {
		return this.allowedClass.isEmpty() || this.allowedClass.contains(clazz);
	}
	
	private <A> Class<A> checkClassAllowed(Class<A> clazz) {
		
		if (!this.isClassAllowed(clazz))
			throw new IllegalArgumentException("Invalid listener class '" + clazz.getSimpleName() + "'.");
		
		return clazz;
		
	}

	private <A> List<A> getListenerGroup(Class<A> listenerClass) {
		return (List<A>) this.eventListeners.computeIfAbsent(this.checkClassAllowed(listenerClass), cl -> new CopyOnWriteArrayList<>());
	}

	private <A> List<A> getListenerGroupUnsafe(Class<A> listenerClass) {
		return (List<A>) this.eventListeners.get(listenerClass);
	}

	// ADD //
	
	public <A> void addEventListener(Class<A> clazz, A listener) {
		this.getListenerGroup(clazz).add(listener);

		/*List<A> group = this.getListenerGroup(clazz);
		
		if (this.firingListeners.contains(clazz)) {
			((List<A>) this.toAddListeners.computeIfAbsent(clazz, cl -> new ArrayList<>())).add(listener);
		} else {
			group.add(listener);
		}*/
		
	}
	
	public <A> void addEventListener(A listener) {
		this.addEventListener((Class<A>) listener.getClass(), listener);
	}

	// REMOVE //
	
	public <A> void removeEventListener(Class<A> clazz, A listener) {
		List<A> group = this.getListenerGroupUnsafe(clazz);
		if (group != null) {
			group.remove(listener);
		}

		/*List<A> group = this.getListenerGroup(clazz);
		
		if (this.firingListeners.contains(clazz)) {
			((List<A>) this.toRemoveListeners.computeIfAbsent(clazz, cl -> new ArrayList<>())).add(listener);
		} else {
			group.remove(listener);
		}*/
		
	}

	public <A> void removeEventListener(A listener) {
		this.removeEventListener((Class<A>) listener.getClass(), listener);
	}

	// FIRE //

	public <A> void fireListeners(Class<A> listenerClass, Consumer<A> listenerCaller) {
		
		List<A> group = this.getListenerGroupUnsafe(listenerClass);

		if (group != null) {
			group.forEach(l -> isolatedListenerCall(l, listenerCaller));
		}

		/*List<A> toAddList = (List<A>) this.toAddListeners.get(listenerClass);
		List<A> toRemoveList = (List<A>) this.toRemoveListeners.get(listenerClass);
		
		if (toAddList != null && !toAddList.isEmpty()) {
			
			group.addAll(toAddList);
			toAddList.clear();
			
		}
		
		if (toRemoveList != null && !toRemoveList.isEmpty()) {
			
			group.removeAll(toRemoveList);
			toRemoveList.clear();
			
		}
		
		this.firingListeners.add(listenerClass);
		
		group.forEach((l) -> {
			
			try {
				listenerCaller.accept(l);
			} catch (RuntimeException ignored) {}
			
		});
		
		this.firingListeners.remove(listenerClass);*/
		
	}

	private static <L> void isolatedListenerCall(L listener, Consumer<L> listenerCaller) {
		try {
			listenerCaller.accept(listener);
		} catch (RuntimeException e) {
			LOGGER.log(Level.SEVERE, "Error when firing listener.", e);
		}
	}

}
