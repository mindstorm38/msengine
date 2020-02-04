package io.msengine.common.util.event;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked"})
public class MethodEventManager {
	
	private final HashMap<Class<?>, List<?>> eventListeners = new HashMap<>();
	private final HashSet<Class<?>> allowedClass = new HashSet<>();
	private final HashSet<Class<?>> firingListeners = new HashSet<>();
	private final HashMap<Class<?>, List<?>> toAddListeners = new HashMap<>();
	private final HashMap<Class<?>, List<?>> toRemoveListeners = new HashMap<>();
	
	public MethodEventManager(Class<?>...allowedClasses) {
		this.allowedClass.addAll(Arrays.asList(allowedClasses));
	}
	
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
		return (List<A>) this.eventListeners.computeIfAbsent(this.checkClassAllowed(listenerClass), cl -> new ArrayList<A>());
	}
	
	public <A> void addEventListener(Class<A> clazz, A listener) {
		
		List<A> group = this.getListenerGroup(clazz);
		
		if (this.firingListeners.contains(clazz)) {
			((List<A>) this.toAddListeners.computeIfAbsent(clazz, cl -> new ArrayList<>())).add(listener);
		} else {
			group.add(listener);
		}
		
	}
	
	public <A> void addEventListener(A listener) {
		this.addEventListener((Class<A>) listener.getClass(), listener);
	}
	
	public <A> void removeEventListener(Class<A> clazz, A listener) {
		
		List<A> group = this.getListenerGroup(clazz);
		
		if (this.firingListeners.contains(clazz)) {
			((List<A>) this.toRemoveListeners.computeIfAbsent(clazz, cl -> new ArrayList<>())).add(listener);
		} else {
			group.remove(listener);
		}
		
	}
	
	public <A> void removeEventListener(A listener) {
		this.removeEventListener((Class<A>) listener.getClass(), listener);
	}
	
	public <A> void fireListeners(Class<A> listenerClass, Consumer<A> listenerCaller) {
		
		List<A> group = this.getListenerGroup(listenerClass);
		List<A> toAddList = (List<A>) this.toAddListeners.get(listenerClass);
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
		
		this.firingListeners.remove(listenerClass);
		
	}
	
}
