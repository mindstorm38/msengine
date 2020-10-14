package io.msengine.client.ngui;

import io.msengine.client.ngui.event.GuiEvent;
import io.msengine.client.ngui.event.GuiEventListener;
import io.msengine.client.ngui.event.GuiEventManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class GuiObject {

	protected static final Logger LOGGER = Logger.getLogger("msengine.gui.object");

	public static final int SIZE_AUTO = -1;
	
	private static final int FLAG_READY     = 0x1;
	private static final int FLAG_DISPLAYED = 0x2;
	private static final int FLAG_VISIBLE   = 0x4;
	
	protected float xPos = 0, yPos = 0;
	protected float width = 0, height = 0;
	protected float xAnchor = 0, yAnchor = 0;
	protected float xOffset = 0, yOffset = 0;
	protected int xIntOffset = 0, yIntOffset = 0;
	
	private byte flags = 0;
	private GuiParent parent = null;

	private GuiEventManager eventManager = null;
	
	// [ Management ] //
	
	protected abstract void init();
	protected abstract void stop();
	abstract void render(float alpha);
	abstract void update();
	
	void innerInit() {
		if (!this.isReady()) {
			try {
				this.init();
				this.setFlag(FLAG_READY, true);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Failed to call init() on " + this.getClass() + ", the object no longer ready.", e);
			}
		}
	}
	
	void innerStop() {
		if (this.isReady()) {
			try {
				this.setFlag(FLAG_READY, false);
				this.stop();
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Failed to call stop() on " + this.getClass() + ", the object no longer ready.", e);
			}
		}
	}
	
	public boolean isReady() {
		return this.hasFlag(FLAG_READY);
	}
	
	void setDisplayed(boolean displayed) {
		this.setFlag(FLAG_DISPLAYED, displayed);
	}
	
	public boolean isDisplayed() {
		return this.hasFlag(FLAG_DISPLAYED);
	}
	
	public void setVisible(boolean visible) {
		this.setFlag(FLAG_VISIBLE, visible);
	}
	
	public boolean isVisible() {
		return this.hasFlag(FLAG_VISIBLE);
	}
	
	public boolean mustRender() {
		return this.hasFlag(FLAG_READY | FLAG_DISPLAYED | FLAG_VISIBLE);
	}
	
	private void setFlag(int mask, boolean enabled) {
		if (enabled) {
			this.flags |= mask;
		} else {
			this.flags &= ~mask;
		}
	}
	
	private boolean hasFlag(int mask) {
		return (this.flags & mask) == mask;
	}
	
	// [ Position ] //
	
	public void setXPos(float xPos) {
		if (this.xPos != xPos) {
			this.xPos = xPos;
			this.updateXOffset();
		}
	}
	
	public void setYPos(float yPos) {
		if (this.yPos != yPos) {
			this.yPos = yPos;
			this.updateYOffset();
		}
	}
	
	public void setPosition(float xPos, float yPos) {
		this.setXPos(xPos);
		this.setYPos(yPos);
	}
	
	public float getXPos() {
		return this.xPos;
	}
	
	public float getYPos() {
		return this.yPos;
	}
	
	// [ Size ] //
	
	public void setWidth(float width) {
		
		if (this.width == width)
			return;
		
		if (width == SIZE_AUTO)
			width = this.getAutoWidth();
		
		if (width < 0)
			throw new IllegalArgumentException("Invalid width given : " + width);
		
		this.width = width;
		this.updateXOffset();
		
	}
	
	public void setHeight(float height) {
		
		if (this.height == height)
			return;
		
		if (height == SIZE_AUTO)
			height = this.getAutoHeight();
		
		if (height < 0)
			throw new IllegalArgumentException("Invalid height given : " + height);
		
		this.height = height;
		this.updateYOffset();
		
	}
	
	public void setSize(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public void setWidthAuto() {
		this.setWidth(SIZE_AUTO);
	}
	
	public void setHeightAuto() {
		this.setHeight(SIZE_AUTO);
	}
	
	public void setSizeAuto() {
		this.setSize(SIZE_AUTO, SIZE_AUTO);
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public float getAutoWidth() {
		return 0;
	}
	
	public float getAutoHeight() {
		return 0;
	}
	
	// [ Anchor ] //
	
	public void setXAnchor(float xAnchor) {
		if (this.xAnchor != xAnchor) {
			this.xAnchor = xAnchor;
			this.updateXOffset();
		}
	}
	
	public void setYAnchor(float yAnchor) {
		if (this.yAnchor != yAnchor) {
			this.yAnchor = yAnchor;
			this.updateYOffset();
		}
	}
	
	public void setAnchor(float xAnchor, float yAnchor) {
		this.setXAnchor(xAnchor);
		this.setYAnchor(yAnchor);
	}
	
	public float getXAnchor() {
		return this.xAnchor;
	}
	
	public float getYAnchor() {
		return this.yAnchor;
	}
	
	// [ Offset ] //
	
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
	
	public float getXOffset() {
		return this.xOffset;
	}
	
	public float getYOffset() {
		return this.yOffset;
	}
	
	public float getOppositeXOffset() {
		return this.xOffset + this.width;
	}
	
	public float getOppositeYOffset() {
		return this.yOffset + this.height;
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
	
	// [ Parent ] //
	
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

	// [ Events ] //

	public GuiEventManager getEventManager() {
		if (this.eventManager == null) {
			this.eventManager = new GuiEventManager();
		}
		return this.eventManager;
	}

	public <E extends GuiEvent> void addEventListener(Class<E> clazz, GuiEventListener<E> listener) {
		this.getEventManager().addEventListener(clazz, listener);
	}

	public <E extends GuiEvent> void removeEventListener(Class<E> clazz, GuiEventListener<E> listener) {
		if (this.eventManager != null) {
			this.eventManager.removeEventListener(clazz, listener);
		}
	}

	public void fireEvent(GuiEvent event) {
		if (this.eventManager != null) {
			this.eventManager.fireGuiEvent(this, event);
		}
	}

	// [ Utils ] //


	@Override
	public String toString() {
		return this.getClass().getSimpleName() +
				"<x=" + this.getXPos() +
				", y=" + this.getYPos() +
				", w=" + this.getWidth() +
				", h=" + this.getHeight() +
				", ax=" + this.getXAnchor() +
				", ay=" + this.getYAnchor() + ">";
	}

}
