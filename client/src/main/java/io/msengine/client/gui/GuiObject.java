package io.msengine.client.gui;

import java.util.ArrayList;
import java.util.List;

import io.msengine.client.gui.event.GuiEvent;
import io.msengine.client.gui.event.GuiListener;
import io.msengine.client.gui.event.GuiListenerGroup;
import io.msengine.client.renderer.gui.GuiRenderer;
import io.msengine.client.renderer.model.ModelHandler;

public abstract class GuiObject {
	
	public static final int SIZE_AUTO = -1;
	
	protected final GuiRenderer renderer;
	protected final ModelHandler model;
	protected final GuiManager manager;
	
	protected float xPos, yPos;
	protected float width, height;
	protected float xAnchor, yAnchor;
	protected float xOffset, yOffset;
	protected int xIntOffset, yIntOffset;
	
	private boolean initied;
	private boolean visible;
	
	private boolean sceneActive;
	
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
		
		this.xIntOffset = 0;
		this.yIntOffset = 0;
		
		this.initied = false;
		this.visible = true;
		
		this.sceneActive = false;
		
		this.listeners = new ArrayList<>();
		
		this.parent = null;
		
	}
	
	// Position //
	public void setXPos(float xPos) {
		
		if ( this.xPos == xPos )
			return;
		
		this.xPos = xPos;
		this.internalUpdateXOffset();
		
	}
	
	public void setYPos(float yPos) {
		
		if ( this.yPos == yPos )
			return;
		
		this.yPos = yPos;
		this.internalUpdateYOffset();
		
	}
	
	public void setPosition(float xPos, float yPos) {
		
		this.setXPos( xPos );
		this.setYPos( yPos );
		
	}
	
	public float getXPos() { return this.xPos; }
	public float getYPos() { return this.yPos; }
	
	// Size //
	public void setWidth(float width) {
		
		if ( this.width == width )
			return;
		
		if ( width == SIZE_AUTO )
			width = this.getAutoWidth();
		
		if ( width < 0 )
			throw new IllegalArgumentException( "Invalid width given : " + width );
		
		this.width = width;
		this.internalUpdateXOffset();
		
	}
	
	public void setHeight(float height) {
		
		if ( this.height == height )
			return;
		
		if ( height == SIZE_AUTO )
			height = this.getAutoHeight();
		
		if ( height < 0 )
			throw new IllegalArgumentException( "Invalid height given : " + height );
		
		this.height = height;
		this.internalUpdateYOffset();
		
	}
	
	public void setSize(float width, float height) {
		
		this.setWidth( width );
		this.setHeight( height );
		
	}
	
	public float getWidth() { return this.width; }
	public float getHeight() { return this.height; }
	
	public float getAutoWidth() { return 0f; }
	public float getAutoHeight() { return 0f; }
	
	// Anchor //
	public void setXAnchor(float xAnchor) {
		
		this.xAnchor = xAnchor;
		this.internalUpdateXOffset();
		
	}
	
	public void setYAnchor(float yAnchor) {
		
		this.yAnchor = yAnchor;
		this.internalUpdateYOffset();
		
	}
	
	public void setAnchor(float xAnchor, float yAnchor) {
		
		this.setXAnchor( xAnchor );
		this.setYAnchor( yAnchor );
		
	}
	
	public float getXAnchor() { return this.xAnchor; }
	public float getYAnchor() { return this.yAnchor; }
	
	/**
	 * Internal method to update X offset and trigger parent child offset update.
	 */
	protected void internalUpdateXOffset() {
		
		this.updateXOffset();
		
		if (this.parent != null)
			this.parent.childXOffsetUpdated(this);
		
	}
	
	/**
	 * Internal method to update Y offset and trigger parent child offset update.
	 */
	protected void internalUpdateYOffset() {
		
		this.updateYOffset();
		
		if (this.parent != null)
			this.parent.childYOffsetUpdated(this);
		
	}
	
	/**
	 * Update the X offset used to render at the right position.
	 */
	public void updateXOffset() {
		
		this.xOffset = (this.xPos + (this.xAnchor + 1f) * (this.width / -2f));
		if (this.parent != null) this.xOffset += this.parent.xOffset;
		this.xIntOffset = Math.round(this.xOffset);
		
	}
	
	/**
	 * Update the Y offset used to render at the right position.
	 */
	public void updateYOffset() {
		
		this.yOffset = (this.yPos + (this.yAnchor + 1f) * (this.height / -2f));
		if (this.parent != null) this.yOffset += this.parent.yOffset;
		this.yIntOffset = Math.round(this.yOffset);
		
	}
	
	/**
	 * Update all offsets used to render at the right position.
	 */
	public void updateOffsets() {
		
		this.updateXOffset();
		this.updateYOffset();
		
	}
	
	/**
	 * @return The X offset for render and interactions.
	 */
	public float getXOffset() {
		return this.xOffset;
	}
	
	/**
	 * @return The Y offset for render and interactions.
	 */
	public float getYOffset() {
		return this.yOffset;
	}
	
	/**
	 * @return The inverted X offset (xOffset + width).
	 */
	public float getOpositeOffsetX() {
		return this.xOffset + this.width;
	}
	
	/**
	 * @return The inverted Y offset (yOffset + height).
	 */
	public float getOpositeOffsetY() {
		return this.yOffset + this.height;
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
		return this.sceneActive && this.initied && this.visible;
	}
	
	/**
	 * Utility method to compute if a point is over this object, mostly used to mouse over detection.
	 * @param x The point X.
	 * @param y The point Y.
	 * @return True if this point is over this object.
	 */
	public boolean isPointOver(int x, int y) {
		
		float xOff = this.xOffset;
		float yOff = this.yOffset;
		
		return x >= xOff && y >= yOff && x < (xOff + this.width) && y < (yOff + this.height);
		
	}
	
	/**
	 * @return True if this object is contained in the ({@link GuiManager}'s current scene.
	 */
	public boolean isSceneActive() {
		return sceneActive;
	}
	
	void setSceneActive(boolean sceneActive) {
		this.sceneActive = sceneActive;
	}
	
	public final GuiParent getParent() {
		return this.parent;
	}
	
	public final boolean hasParent() {
		return this.parent != null;
	}
	
	void setParent(GuiParent parent) {
		
		this.parent = parent;
		this.updateOffsets();
		
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
			if ( eventClass.equals( group.getEventClass() ) )
				return (GuiListenerGroup<E>) group;
		return null;
	}
	
	public <E extends GuiEvent> void addEventListener(Class<E> eventClass, GuiListener<E> listener) {
		
		GuiListenerGroup<E> group = this.getListenerGroup( eventClass );
		
		if ( group == null ) {
			
			group = new GuiListenerGroup<>( eventClass );
			this.listeners.add( group );
			
		}
		
		group.getListeners().add( listener );
		
	}
	
	@SuppressWarnings("unchecked")
	public <E extends GuiEvent> void fireEvent(E event) {
		
		event.setOrigin( this );
		
		for ( GuiListenerGroup<?> group : this.listeners ) {
			
			if ( group.getEventClass().isAssignableFrom( event.getClass() ) ) {
				
				GuiListenerGroup<E> grp = (GuiListenerGroup<E>) group;
				
				for ( GuiListener<E> listener : grp.getListeners() )
					listener.guiEvent( event );
				
			}
			
		}
		
	}
	
}
