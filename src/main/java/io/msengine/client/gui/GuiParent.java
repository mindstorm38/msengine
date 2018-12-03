package io.msengine.client.gui;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiParent extends GuiObject {
	
	private final List<GuiObject> children;
	
	public GuiParent() {
		
		super();
		
		this.children = new ArrayList<>();
		
	}
	
	public boolean hasChild(GuiObject child) {
		return this.children.contains( child );
	}
	
	public boolean addChild(GuiObject child) {
		if ( this.hasChild( child ) ) return false;
		return this.children.add( child );
	}
	
	public boolean removeChild(GuiObject child) {
		return this.children.remove( child );
	}
	
}
