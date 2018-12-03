package io.msengine.client.gui;

import java.util.ArrayList;
import java.util.List;

import io.msengine.client.gui.event.GuiEvent;
import io.msengine.client.gui.event.GuiListener;
import io.msengine.client.gui.event.GuiListenerGroup;

public abstract class GuiObject {
	
	protected float xPos, yPos;
	protected float width, height;
	protected float xAnchor, yAnchor;
	
	private final List<GuiListenerGroup<?>> listeners;
	
	public GuiObject() {
		
		this.xPos = 0;
		this.yPos = 0;
		
		this.width = 0;
		this.height = 0;
		
		this.xAnchor = 0;
		this.yAnchor = 0;
		
		this.listeners = new ArrayList<>();
		
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
	
	@SuppressWarnings("unchecked")
	public <E extends GuiEvent<?>> GuiListenerGroup<E> getListenerGroup(Class<E> eventClass) {
		for ( GuiListenerGroup<?> group : this.listeners )
			if ( eventClass.equals( group.getClass() ) )
				return (GuiListenerGroup<E>) group;
		return null;
	}
	
	public <E extends GuiEvent<?>> void addEventListener(Class<E> eventClass, GuiListener<E> listener) {
		
		GuiListenerGroup<E> group = this.getListenerGroup( eventClass );
		
		if ( group == null ) {
			
			group = new GuiListenerGroup<E>( eventClass );
			this.listeners.add( group );
			
		}
		
		group.getListeners().add( listener );
		
	}
	
	@SuppressWarnings("unchecked")
	public <E extends GuiEvent<?>> void fireEvent(E event) {
		
		for ( GuiListenerGroup<?> group : this.listeners ) {
			
			if ( group.getClass().isAssignableFrom( event.getClass() ) ) {
				
				GuiListenerGroup<E> grp = (GuiListenerGroup<E>) group;
				
				for ( GuiListener<E> listener : grp.getListeners() )
					listener.guiEvent( event );
				
			}
			
		}
		
	}
	
}
