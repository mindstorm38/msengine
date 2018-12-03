package io.msengine.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.msengine.client.gui.event.GuiEvent;
import io.msengine.client.gui.event.GuiListener;

public abstract class GuiObject {
	
	protected float xPos, yPos;
	protected float width, height;
	protected float xAnchor, yAnchor;
	
	private final Map<Class<? extends GuiEvent<?>>, List<GuiListener<?>>> eventListeners;
	
	public GuiObject() {
		
		this.xPos = 0;
		this.yPos = 0;
		
		this.width = 0;
		this.height = 0;
		
		this.xAnchor = 0;
		this.yAnchor = 0;
		
		this.eventListeners = new HashMap<>();
		
	}
	
	public void setXPos(float xPos) { this.xPos = xPos; }
	public void setYPos(float yPos) { this.yPos = yPos; }
	
	public float getXPos() { return this.xPos; }
	public float getYPos() { return this.yPos; }
	
	public void setWidth(float width) { this.width = width; }
	public void setHeight(float height) { this.height = height; }
	
	public float getWidth() { return this.width; }
	public float getHeight() { return this.height; }
	
	public void setXAnchor(float xAnchor) { this.xAnchor = xAnchor; }
	public void setYAnchor(float yAnchor) { this.yAnchor = yAnchor; }
	
	public float getXAnchor() { return this.xAnchor; }
	public float getYAnchor() { return this.yAnchor; }
	
	public abstract void init();
	public abstract void stop();
	
	public abstract void render(float alpha);
	public abstract void update();
	
	public <E extends GuiEvent<?>> void addEventListener(Class<E> eventClass, GuiListener<E> listener) {
		
		List<GuiListener<?>> listeners = this.eventListeners.get( eventClass );
		
		if ( listeners == null ) {
			
			listeners = new ArrayList<>();
			this.eventListeners.put( eventClass, listeners );
			
		}
		
		listeners.add( listener );
		
	}
	
}
