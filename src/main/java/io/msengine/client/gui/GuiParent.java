package io.msengine.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * Representing a special {@link GuiObject} that can contains other {@link GuiObject}.
 * 
 * @author Mindstorm38
 *
 */
public abstract class GuiParent extends GuiObject {
	
	private final List<GuiObject> children;
	
	public GuiParent() {
		
		super();
		
		this.children = new ArrayList<>();
		
	}
	
	@Override
	public void init() {
		
		this.children.forEach( this::initChild );
		
	}
	
	@Override
	public void stop() {
		
		this.children.forEach( this::stopChild );
		
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
	
	public boolean hasChild(GuiObject child) {
		return this.children.contains( child );
	}
	
	/**
	 * Add a child {@link GuiObject} to this parent.<br>
	 * If the {@link GuiObject} is already added to another parent, an {@link IllegalArgumentException} is thrown.
	 * @param child The child object to add
	 * @return <code>true</code> if the internal children list was modified
	 */
	public boolean addChild(GuiObject child) {
		
		if ( this.hasChild( child ) ) return false;
		if ( child.hasParent() ) throw new IllegalArgumentException("This GuiObject is already bound to another GuiParent");
		
		if ( this.children.add( child ) ) {
			
			child.setParent( this );
			this.initChild( child );
			
			return true;
			
		} else return false;
		
	}
	
	/**
	 * Remove a child {@link GuiObject} from this parent.
	 * @param child The child object to remove
	 * @return <code>true</code> if the child was removed
	 */
	public boolean removeChild(GuiObject child) {
		
		if ( this.children.remove( child ) ) {
			
			child.setParent( null );
			this.stopChild( child );
			
			return true;
			
		} else return false;
		
	}
	
}
