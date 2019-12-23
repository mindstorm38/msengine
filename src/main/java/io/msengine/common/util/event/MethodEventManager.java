package io.msengine.common.util.event;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked"})
public class MethodEventManager {
	
	private final Map<Class<?>, List<?>> eventListeners = new HashMap<>();
	private final Set<Class<?>> allowedClass = new HashSet<>();
	
	public MethodEventManager(Class<?>...allowedClasses) {
		this.allowedClass.addAll(Arrays.asList(allowedClasses));
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
		return (List<A>) this.eventListeners.computeIfAbsent(listenerClass, cl -> new ArrayList<A>());
	}
	
	public <A> void addEventListener(Class<A> clazz, A listener) {
		this.getListenerGroup(this.checkClassAllowed(clazz)).add(listener);
	}
	
	public <A> void addEventListener(A listener) {
		this.addEventListener((Class<A>) listener.getClass(), listener);
	}
	
	public <A> void removeEventListener(Class<A> clazz, A listener) {
		this.getListenerGroup(this.checkClassAllowed(clazz)).remove(listener);
	}
	
	public <A> void removeEventListener(A listener) {
		this.removeEventListener((Class<A>) listener.getClass(), listener);
	}
	
	public <A> void fireListeners(Class<A> listenerClass, Consumer<A> listenerCaller) {
		
		this.getListenerGroup(listenerClass).forEach((l) -> {
			
			try {
				listenerCaller.accept(l);
			} catch (RuntimeException ignored) {}
			
		});
		
	}
	
}
