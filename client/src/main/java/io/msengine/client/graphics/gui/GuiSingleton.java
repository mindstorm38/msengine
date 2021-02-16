package io.msengine.client.graphics.gui;

public abstract class GuiSingleton<T> {
	
	public abstract T supply(GuiManager manager);
	public abstract void release(T obj);
	
	@SuppressWarnings("unchecked")
	public void releaseRaw(Object obj) {
		this.release((T) obj);
	}

}
