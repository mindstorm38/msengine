package io.msengine.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * Representing a special {@link GuiObject} that can contains other {@link GuiObject}.
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class GuiParent extends GuiObject {
	
	private final List<GuiObject> children;
	
	public GuiParent() {
		
		super();
		
		this.children = new ArrayList<>();
		
	}
	
	@Override
	public void init() {
		this.children.forEach(this::initChild);
	}
	
	@Override
	public void stop() {
		this.children.forEach(this::stopChild);
	}
	
	@Override
	public void render(float alpha) {
		
		for ( GuiObject child : this.children )
			if ( child.renderable() )
				child.render( alpha );
		
	}
	
	@Override
	public void update() {
		
		for ( GuiObject child : this.children )
			if ( child.renderable() )
				child.update();
		
	}
	
	private void initChild(GuiObject child) {
		
		if ( this.usable() ) {
			
			try {
				
				child._init();
				
			} catch (RuntimeException e) {
				LOGGER.log( Level.SEVERE, "An error was thrown when initing the GuiObject '" + this.getClass().getSimpleName() + "'", e );
			}
			
		}
		
	}
	
	private void stopChild(GuiObject child) {
		
		if ( this.usable() ) {
			
			try {
				
				child._stop();
				
			} catch (RuntimeException e) {
				LOGGER.log( Level.SEVERE, "An error was thrown when stoping the GuiObject '" + this.getClass().getSimpleName() + "'", e );
			}
			
		}
		
	}
	
	/**
	 * Get if a child owned by this parent.
	 * @param child This child to test.
	 * @return True if this child is owned by this parent.
	 */
	public boolean hasChild(GuiObject child) {
		return this.children.contains( child );
	}
	
	public void checkHasChild(GuiObject child) {
		
		if (!this.hasChild(child))
			throw new IllegalArgumentException("This child is not owned by this parent.");
		
	}
	
	@Override
	public void updateXOffset() {
		
		super.updateXOffset();
		this.children.forEach(GuiObject::updateXOffset);
		
	}
	
	@Override
	public void updateYOffset() {
		
		super.updateYOffset();
		this.children.forEach(GuiObject::updateYOffset);
		
	}
	
	public void childXOffsetUpdated(GuiObject child) {
		this.checkHasChild(child);
	}
	
	public void childYOffsetUpdated(GuiObject child) {
		this.checkHasChild(child);
	}
	
	@Override
	void setSceneActive(boolean sceneActive) {
		
		super.setSceneActive(sceneActive);
		
		for (GuiObject child : this.children) {
			child.setSceneActive(sceneActive);
		}
		
	}
	
	/**
	 * Add a child {@link GuiObject} to this parent.<br>
	 * If the {@link GuiObject} is already added to another parent, an {@link IllegalArgumentException} is thrown.
	 * @param child The child object to add.
	 * @param index The index to insert the child at.
	 * @return <code>true</code> if the internal children list was modified.
	 */
	public boolean addChild(GuiObject child, int index) {
		
		if (this.hasChild(child))
			return false;
		
		if (child.hasParent())
			throw new IllegalArgumentException("This GuiObject is already bound to another GuiParent");
		
		this.children.add(index, child);
		
		try {
			child.setParent(this);
		} catch (Exception e) {
			
			this.children.remove(child);
			throw e;
			
		}
		
		this.initChild(child);
		child.setSceneActive(this.isSceneActive());
		
		return true;
		
	}
	
	/**
	 * Add a child to this parent <b>at the last position</b>.<br>
	 * @param child The child object to add.
	 * @return <code>true</code> if the internal children list was modified.
	 * @see #addChild(GuiObject, int)
	 */
	public boolean addChild(GuiObject child) {
		return this.addChild(child, this.children.size());
	}
	
	/**
	 * Add a child to this parent before another object.
	 * @param child The child object to add.
	 * @param beforeIt The other child that is already in this parent.
	 * @return <code>true</code> if the internal children list was modified.
	 * @see #addChild(GuiObject, int)
	 */
	public boolean addChild(GuiObject child, GuiObject beforeIt) {
		
		if (child == beforeIt)
			return false;
		
		int index = this.children.indexOf(beforeIt);
		
		if (index == -1)
			return false;
		
		return this.addChild(child, index);
		
	}
	
	/**
	 * Remove a child {@link GuiObject} from this parent.
	 * @param child The child object to remove
	 * @return <code>true</code> if the child was removed
	 */
	public boolean removeChild(GuiObject child) {
		
		if ( this.children.remove( child ) ) {
			
			try {
				child.setParent(null);
			} catch (Exception ignored) {}
			
			this.stopChild(child);
			
			return true;
			
		} else return false;
		
	}
	
}
