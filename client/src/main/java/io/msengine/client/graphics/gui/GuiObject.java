package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.gui.event.GuiEvent;
import io.msengine.client.graphics.gui.event.GuiEventListener;
import io.msengine.client.graphics.gui.event.GuiEventManager;
import io.msengine.client.graphics.gui.render.GuiProgramType;
import io.msengine.client.graphics.shader.ShaderProgram;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.window.Window;
import io.msengine.common.util.event.MethodEventManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class GuiObject {

	protected static final Logger LOGGER = Logger.getLogger("mse.gui.object");

	public static final int SIZE_AUTO = -1;
	
	private static final int FLAG_READY      = 0x1;
	private static final int FLAG_DISPLAYED  = 0x2;
	private static final int FLAG_VISIBLE    = 0x4;
	private static final int FLAG_CURSOR_OVER = 0x8;
	
	public static final float LEFT = -1f;
	public static final float UP = -1f;
	public static final float CENTER = 0f;
	public static final float RIGHT = 1f;
	public static final float BOTTOM = 1f;
	
	/** The anchor coordinates. */
	protected float xPos, yPos;
	/** The anchor relative position in the component, -1 for LEFT/UP, 0 for CENTER, 1 for RIGHT/BOTTOM. */
	protected float xAnchor = LEFT, yAnchor = UP;
	/** The anchor to the parent. */
	protected float xSupAnchor = LEFT, ySupAnchor = UP;
	/** The target size, default to automatic size. */
	protected float width = SIZE_AUTO, height = SIZE_AUTO;
	/** The real width, can be either target size, or automatic size. */
	protected float realWidth, realHeight;
	/** The real top/left coordinates of the component. */
	protected float xOffset, yOffset;
	/** The real top/left coordinates of the component rounded to integer. */
	protected int xIntOffset, yIntOffset;
	
	private byte flags = FLAG_VISIBLE; // Visible by default
	private GuiParent parent;
	protected GuiManager manager;
	protected ModelHandler model;
	
	private GuiEventManager eventManager;
	
	// [ Management ] //
	
	protected abstract void init();
	protected abstract void stop();
	protected abstract void render(float alpha);
	protected abstract void update();
	
	void innerInit(GuiManager manager) {
		if (!this.isReady()) {
			try {
				this.setFlag(FLAG_READY, true);
				this.manager = manager;
				this.model = manager.getModel();
				this.init();
			} catch (Exception e) {
				this.setFlag(FLAG_READY, false);
				LOGGER.log(Level.SEVERE, "Failed to call init() on " + this.getClass() + ", the object no longer ready. " +
						"Errors in init() must be fixed because they can cause future errors in stop() and render/update methods.", e);
			}
		}
	}
	
	void innerStop() {
		if (this.isReady()) {
			try {
				this.stop();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to call stop() on " + this.getClass() + ", the object no longer ready. " +
						"Errors in stop() must be fixed because they can cause memory leaks or alter future calls to init().", e);
			} finally {
				this.model = null;
				this.manager = null;
				this.setFlag(FLAG_READY, false);
			}
		}
	}
	
	// [ Flags ] //
	
	public boolean isReady() {
		return this.hasFlag(FLAG_READY);
	}
	
	public boolean isDisplayed() {
		return this.hasFlag(FLAG_DISPLAYED);
	}
	
	void setDisplayed(boolean displayed) {
		this.setFlag(FLAG_DISPLAYED, displayed);
	}
	
	public boolean isVisible() {
		return this.hasFlag(FLAG_VISIBLE);
	}
	
	public void setVisible(boolean visible) {
		this.setFlag(FLAG_VISIBLE, visible);
	}
	
	public boolean mustRender() {
		return this.hasFlag(FLAG_READY | FLAG_DISPLAYED | FLAG_VISIBLE);
	}
	
	public boolean isCursorOver() {
		return this.hasFlag(FLAG_CURSOR_OVER);
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
	
	// [ Programs ] //
	
	public GuiManager getManager() {
		return this.manager;
	}
	
	public Window getWindow() {
		return this.manager.getWindow();
	}
	
	/**
	 * @deprecated Use {@link #getWindow()} instead and use direct methods like {@link Window#addEventListener(Class, Object)}
	 */
	@Deprecated
	public MethodEventManager getWindowEventManager() {
		return this.manager.getWindow().getEventManager();
	}
	
	public ModelHandler getModel() {
		return this.model;
	}
	
	// [ Programs ] //
	
	/** * @see GuiManager#acquireProgram(GuiProgramType) */
	protected <P extends ShaderProgram> P acquireProgram(GuiProgramType<P> type) {
		return this.manager.acquireProgram(type);
	}
	
	/** @see GuiManager#releaseProgram(GuiProgramType) */
	protected void releaseProgram(GuiProgramType<?> type) {
		this.manager.releaseProgram(type);
	}
	
	/** @see GuiManager#getProgram(GuiProgramType) */
	protected <P extends ShaderProgram> P getProgram(GuiProgramType<P> type) {
		return this.manager.getProgram(type);
	}
	
	/** @see GuiManager#useProgram(GuiProgramType) */
	protected <P extends ShaderProgram> P useProgram(GuiProgramType<P> type) {
		return this.manager.useProgram(type);
	}
	
	// [ Position ] //
	
	/** Callback method called when {@link #xPos} was changed (using {@link #setXPos(float)}). */
	protected void onXPosChanged() { }
	
	/** Callback method called when {@link #yPos} was changed (using {@link #setYPos(float)}). */
	protected void onYPosChanged() { }
	
	public void setXPos(float xPos) {
		if (this.xPos != xPos) {
			this.xPos = xPos;
			this.updateXOffset();
			this.onXPosChanged();
		}
	}
	
	public void setYPos(float yPos) {
		if (this.yPos != yPos) {
			this.yPos = yPos;
			this.updateYOffset();
			this.onYPosChanged();
		}
	}
	
	public void setPos(float xPos, float yPos) {
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
	
	/** Callback method called when preferred {@link #width} was changed (using {@link #setWidth(float)}). */
	protected void onWidthChanged() { }
	
	/** Callback method called when preferred {@link #height} was changed (using {@link #setHeight(float)}). */
	protected void onHeightChanged() { }
	
	public void setWidth(float width) {
		if (this.width != width) {
			this.width = width;
			this.updateXOffset();
			this.onWidthChanged();
		}
	}
	
	public void setHeight(float height) {
		if (this.height != height) {
			this.height = height;
			this.updateYOffset();
			this.onHeightChanged();
		}
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
	
	public boolean isAutoWidth() {
		return this.width < 0;
	}
	
	public boolean isAutoHeight() {
		return this.height < 0;
	}
	
	// [ Anchor ] //
	
	/** Callback method called when {@link #xAnchor} was changed (using {@link #setXAnchor(float)}). */
	protected void onXAnchorChanged() { }
	
	/** Callback method called when {@link #yAnchor} was changed (using {@link #setYAnchor(float)}). */
	protected void onYAnchorChanged() { }
	
	public void setXAnchor(float xAnchor) {
		if (this.xAnchor != xAnchor) {
			this.xAnchor = xAnchor;
			this.updateXOffset();
			this.onXAnchorChanged();
		}
	}
	
	public void setYAnchor(float yAnchor) {
		if (this.yAnchor != yAnchor) {
			this.yAnchor = yAnchor;
			this.updateYOffset();
			this.onYAnchorChanged();
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
	
	/** Callback method called when {@link #xSupAnchor} was changed (using {@link #setXSupAnchor(float)}). */
	protected void onXSupAnchorChanged() { }
	
	/** Callback method called when {@link #ySupAnchor} was changed (using {@link #setYSupAnchor(float)}). */
	protected void onYSupAnchorChanged() { }
	
	public void setXSupAnchor(float xSupAnchor) {
		if (this.xSupAnchor != xSupAnchor) {
			this.xSupAnchor = xSupAnchor;
			this.updateXOffset();
			this.onXAnchorChanged();
		}
	}
	
	public void setYSupAnchor(float ySupAnchor) {
		if (this.ySupAnchor != ySupAnchor) {
			this.ySupAnchor = ySupAnchor;
			this.updateYOffset();
			this.onYAnchorChanged();
		}
	}
	
	public void setSupAnchor(float xSupAnchor, float ySupAnchor) {
		this.setXSupAnchor(xSupAnchor);
		this.setYSupAnchor(ySupAnchor);
	}
	
	public float getXSupAnchor() {
		return this.xSupAnchor;
	}
	
	public float getYSupAnchor() {
		return this.ySupAnchor;
	}
	
	// [ Offset ] //
	
	/** Callback method called when {@link #xOffset} was changed (using {@link #updateXOffset()}). */
	protected void onXOffsetChanged() { }
	
	/** Callback method called when {@link #yOffset} was changed (using {@link #updateYOffset()}). */
	protected void onYOffsetChanged() { }
	
	/** Callback method called when {@link #realWidth} was changed (using {@link #updateXOffset()}). */
	protected void onRealWidthChanged() { }
	
	/** Callback method called when {@link #realHeight} was changed (using {@link #updateYOffset()}). */
	protected void onRealHeightChanged() { }
	
	/** Callback method called when either {@link #xOffset} or {@link #realWidth} has changed (using {@link #updateXOffset()}). */
	protected void onXShapeChanged() { }
	
	/** Callback method called when either {@link #yOffset} or {@link #realHeight} has changed (using {@link #updateYOffset()}). */
	protected void onYShapeChanged() { }
	
	/**
	 * Update the {@link #xOffset} and {@link #realWidth} used to compute the left position of the component.
	 */
	public void updateXOffset() {
		
		boolean changed = false;
		
		float width = this.isAutoWidth() ? this.getAutoWidth() : this.width;
		if (this.realWidth != width) {
			this.realWidth = width;
			changed = true;
			this.onRealWidthChanged();
		}
		
		float off = this.xPos + (this.xAnchor + 1f) * (width / -2f);
		if (this.parent != null) {
			off += this.parent.xOffset + (this.xSupAnchor + 1f) * (this.parent.realWidth / 2f);
		}
		
		if (this.xOffset != off) {
			this.xOffset = off;
			this.xIntOffset = Math.round(this.xOffset);
			changed = true;
			this.onXOffsetChanged();
		}
		
		if (changed) {
			this.onXShapeChanged();
		}
		
	}
	
	/**
	 * Update the {@link #yOffset} and {@link #realHeight} used to compute the top position of the component.
	 */
	public void updateYOffset() {
		
		boolean changed = false;
		
		float height = this.isAutoHeight() ? this.getAutoHeight() : this.height;
		if (this.realHeight != height) {
			this.realHeight = height;
			changed = true;
			this.onRealHeightChanged();
		}
		
		float off = this.yPos + (this.yAnchor + 1f) * (height / -2f);
		if (this.parent != null) {
			off += this.parent.yOffset + (this.ySupAnchor + 1f) * (this.parent.realHeight / 2f);
		}
		
		if (this.yOffset != off) {
			this.yOffset = off;
			this.yIntOffset = Math.round(this.yOffset);
			changed = true;
			this.onYOffsetChanged();
		}
		
		if (changed) {
			this.onYShapeChanged();
		}
		
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
		return this.xOffset + this.realWidth;
	}
	
	public float getOppositeYOffset() {
		return this.yOffset + this.realHeight;
	}
	
	public float getXOffsetFromParent() {
		return this.parent == null ? 0f : (this.xOffset - this.parent.xOffset);
	}
	
	public float getYOffsetFromParent() {
		return this.parent == null ? 0f : (this.yOffset - this.parent.yOffset);
	}
	
	public float getRealWidth() {
		return this.realWidth;
	}
	
	public float getRealHeight() {
		return this.realHeight;
	}
	
	// [ Mouse over ] //
	
	protected void onMouseOverChanged(boolean over) { }
	
	/**
	 * Set internal "cursor over" flag and fire callbacks and event if values has changed.
	 * @param over True if the mouse if over.
	 */
	protected void setCursorOver(boolean over) {
		if (this.hasFlag(FLAG_CURSOR_OVER) != over) {
			this.setFlag(FLAG_CURSOR_OVER, over);
			this.onMouseOverChanged(over);
			this.fireEvent(new MouseOverEvent(over));
		}
	}
	
	/**
	 * Update the internal "cursor over" flag according to {@link #isPointOver(float, float)}.
	 * @param x The mouse x pos.
	 * @param y The mouse y pos.
	 * @return True if this object is blocking the mouse for neighbors children behind it.
	 */
	protected boolean updateCursorOver(float x, float y) {
		boolean over = this.isPointOver(x, y);
		this.setCursorOver(over);
		return over;
	}
	
	/**
	 * <p>Force this element (and all elements in if this object is a parent) to set mouse over flag to false.</p>
	 * <p>This method is called by the manager when the cursor leaves the window boundaries.</p>
	 */
	protected void updateCursorNotOver() {
		this.setCursorOver(false);
	}
	
	/**
	 * <p>Utility method to compute if a point is over this object, mostly used to mouse over detection.</p>
	 * <p>This method is used to compute "over" status of objects and can be overridden for specific bounds.</p>
	 * @param x The point X.
	 * @param y The point Y.
	 * @return True if this point is over this object.
	 */
	public boolean isPointOver(float x, float y) {
		float xOff = this.xOffset;
		float yOff = this.yOffset;
		return x >= xOff && y >= yOff && x < (xOff + this.realWidth) && y < (yOff + this.realHeight);
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

	public <E extends GuiEvent> void addEventListener(Class<E> clazz, GuiEventListener<? super E> listener) {
		this.getEventManager().addEventListener(clazz, listener);
	}

	public <E extends GuiEvent> void removeEventListener(Class<E> clazz, GuiEventListener<? super E> listener) {
		if (this.eventManager != null) {
			this.eventManager.removeEventListener(clazz, listener);
		}
	}

	public void fireEvent(GuiEvent event) {
		if (this.eventManager != null) {
			this.eventManager.fireGuiEvent(this, event);
		}
	}
	
	// [ Common events ] //
	
	public void addMouseOverEventListener(GuiEventListener<? super MouseOverEvent> listener) {
		this.addEventListener(MouseOverEvent.class, listener);
	}
	
	public static class MouseOverEvent extends GuiEvent {
		private final boolean over;
		public MouseOverEvent(boolean over) {
			this.over = over;
		}
		public boolean isMouseOver() {
			return this.over;
		}
	}

	// [ Utils ] //
	
	/**
	 * Build an event listener to call this object's <code>manager.loadScene(sceneIdentifier)</code>.
	 * @param sceneIdentifier The scene identifier.
	 * @return The event listener.
	 */
	protected GuiEventListener<GuiEvent> buildLoadSceneListener(String sceneIdentifier) {
		return (e) -> this.manager.loadScene(sceneIdentifier);
	}
	
	/**
	 * Build the toString() representation, this must be of the format <b><code>ClassName&lt;prop1=val, prop2=val, ...&gt;</code></b>.
	 * This method may be overridden.
	 * @param builder The representation builder.
	 */
	protected void buildToString(StringBuilder builder) {
		builder.append("pos=").append(this.xPos).append('/').append(this.yPos);
		builder.append(", size=").append(this.width).append('/').append(this.height);
		builder.append(", anchor=").append(this.xAnchor).append('/').append(this.yAnchor);
		builder.append(", offset=").append(this.xOffset).append('/').append(this.yOffset);
		builder.append(", rsize=").append(this.realWidth).append('/').append(this.realHeight);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName()).append('<');
		this.buildToString(builder);
		builder.append('>');
		return builder.toString();
	}
	
}
