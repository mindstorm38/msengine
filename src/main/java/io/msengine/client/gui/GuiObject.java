package io.msengine.client.gui;

import java.util.ArrayList;
import java.util.List;

import io.msengine.client.gui.event.GuiEvent;
import io.msengine.client.gui.event.GuiListener;
import io.msengine.client.gui.event.GuiListenerGroup;
import io.msengine.client.renderer.gui.GuiRenderer;
import io.msengine.client.renderer.model.ModelHandler;

public abstract class GuiObject {
	
	protected final GuiRenderer renderer;
	protected final ModelHandler model;
	protected final GuiManager manager;
	
	protected float xPos, yPos;
	protected float width, height;
	protected float xAnchor, yAnchor;
	protected float xOffset, yOffset;
	
	private boolean initied;
	private boolean visible;
	
	private final List<GuiListenerGroup<?>> listeners;
	
	private GuiParent parent;
	
	public GuiObject() {
		
		this.renderer = GuiRenderer.getInstance();
		this.model = this.renderer.model();
		this.manager = GuiManager.getInstance();
		
		this.xPos = 0;
		this.yPos = 0;
		
		this.width = 0;
		this.height = 0;
		
		this.xAnchor = 0;
		this.yAnchor = 0;
		
		this.xOffset = 0;
		this.yOffset = 0;
		
		this.initied = false;
		this.visible = true;
		
		this.listeners = new ArrayList<>();
		
		this.parent = null;
		
	}
	
	public void setXPos(float xPos) {
		
		this.xPos = xPos;
		this.updateXOffset();
		
	}
	
	public void setYPos(float yPos) {
		
		this.yPos = yPos;
		this.updateYOffset();
		
	}
	
	public void setPosition(float xPos, float yPos) {
		
		this.setXPos( xPos );
		this.setYPos( yPos );
		
	}
	
	public float getXPos() { return this.xPos; }
	public float getYPos() { return this.yPos; }
	
	public void setWidth(float width) {
		
		this.width = width;
		this.updateXOffset();
		
	}
	
	public void setHeight(float height) {
		
		this.height = height;
		this.updateYOffset();
		
	}
	
	public void setSize(float width, float height) {
		
		this.setWidth( width );
		this.setHeight( height );
		
	}
	
	public float getWidth() { return this.width; }
	public float getHeight() { return this.height; }
	
	public void setXAnchor(float xAnchor) {
		
		this.xAnchor = xAnchor;
		this.updateXOffset();
		
	}
	
	public void setYAnchor(float yAnchor) {
		
		this.yAnchor = yAnchor;
		this.updateYOffset();
		
	}
	
	public void setAnchor(float xAnchor, float yAnchor) {
		
		this.setXAnchor( xAnchor );
		this.setYAnchor( yAnchor );
		
	}
	
	public float getXAnchor() { return this.xAnchor; }
	public float getYAnchor() { return this.yAnchor; }
	
	public void updateXOffset() {
		this.xOffset = ( this.xPos + ( this.xAnchor + 1f ) * ( -this.width / 2f ) ) + 1f;
	}
	
	public void updateYOffset() {
		this.yOffset = ( this.yPos + ( this.yAnchor + 1f ) * ( -this.height / 2f ) ) + 1f;
	}
	
	public float getXOffset() {
		return this.xOffset;
	}
	
	public float getYOffset() {
		return this.yOffset;
	}
	
	public void updateOffsets() {
		
		this.updateXOffset();
		this.updateYOffset();
		
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public boolean usable() {
		return this.initied;
	}
	
	public boolean renderable() {
		return this.initied && this.visible;
	}
	
	public GuiParent getParent() {
		return this.parent;
	}
	
	public boolean hasParent() {
		return this.parent != null;
	}
	
	void setParent(GuiParent parent) {
		this.parent = parent;
	}
	
	void _init() {
		
		if ( this.initied ) throw new IllegalStateException("This GuiObject is already initied");
		
		this.initied = true;
		
		this.init();
			
	}
	
	void _stop() {
		
		if ( !this.initied ) throw new IllegalStateException("This GuiObject is not initied");
	
		this.stop();
		
		this.initied = false;
		
	}
	
	protected abstract void init();
	protected abstract void stop();
	
	public abstract void render(float alpha);
	public abstract void update();
	
	@SuppressWarnings("unchecked")
	public <E extends GuiEvent> GuiListenerGroup<E> getListenerGroup(Class<E> eventClass) {
		for ( GuiListenerGroup<?> group : this.listeners )
			if ( eventClass.equals( group.getClass() ) )
				return (GuiListenerGroup<E>) group;
		return null;
	}
	
	public <E extends GuiEvent> void addEventListener(Class<E> eventClass, GuiListener<E> listener) {
		
		GuiListenerGroup<E> group = this.getListenerGroup( eventClass );
		
		if ( group == null ) {
			
			group = new GuiListenerGroup<E>( eventClass );
			this.listeners.add( group );
			
		}
		
		group.getListeners().add( listener );
		
	}
	
	@SuppressWarnings("unchecked")
	public <E extends GuiEvent> void fireEvent(E event) {
		
		event.setOrigin( this );
		
		for ( GuiListenerGroup<?> group : this.listeners ) {
			
			if ( group.getClass().isAssignableFrom( event.getClass() ) ) {
				
				GuiListenerGroup<E> grp = (GuiListenerGroup<E>) group;
				
				for ( GuiListener<E> listener : grp.getListeners() )
					listener.guiEvent( event );
				
			}
			
		}
		
	}
	
}
