package io.msengine.client.ngui;

import java.util.ArrayList;
import java.util.List;

public class GuiParent extends GuiObject {
	
	private final List<GuiObject> children = new ArrayList<>();
	
	@Override
	protected void init() {
		this.children.forEach(this::initChild);
	}
	
	@Override
	protected void stop() {
		this.children.forEach(this::stopChild);
	}
	
	private void initChild(GuiObject child) {
		if (this.isReady()) {
			try {
				child.innerInit();
			} catch (RuntimeException e) {
				//
			}
		}
	}
	
	private void stopChild(GuiObject child) {
		if (this.isReady()) {
			try {
				child.innerStop();
			} catch (RuntimeException e) {
				//
			}
		}
	}
	
	@Override
	void render(float alpha) {
		for (GuiObject child : this.children) {
			if (child.mustRender()) {
				child.render(alpha);
			}
		}
	}
	
	@Override
	void update() {
		for (GuiObject child : this.children) {
			if (child.mustRender()) {
				child.update();
			}
		}
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
	
	@Override
	void setDisplayed(boolean displayed) {
		super.setDisplayed(displayed);
		for (GuiObject child : this.children) {
			child.setDisplayed(displayed);
		}
	}
	
	public boolean hasChild(GuiObject child) {
		return this.children.contains(child);
	}
	
	/**
	 * Add a child {@link GuiObject} to this parent.<br>
	 * If the {@link GuiObject} is already added to another parent, an {@link IllegalArgumentException} is thrown.
	 * @param child The child object to add.
	 * @param index The index to insert the child at.
	 * @return <code>true</code> if the internal children list was modified.
	 */
	public boolean addChild(GuiObject child, int index) {
		
		if (child.hasParent())
			throw new IllegalArgumentException("This GuiObject is already bound to another GuiParent");
		
		if (this.hasChild(child))
			return false;
		
		this.children.add(index, child);
		
		try {
			child.setParent(this);
			if (!child.hasParent()) {
				throw new IllegalArgumentException("This given child GuiObject had not set the parent after a call to GuiObject.setParent(GuiParent)");
			}
		} catch (Exception e) {
			this.children.remove(index);
			throw e;
		}
		
		this.initChild(child);
		child.setDisplayed(this.isDisplayed());
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
		if (this.children.remove(child)) {
			try {
				child.setParent(null);
			} finally {
				this.stopChild(child);
			}
			return true;
		} else {
			return false;
		}
	}
	
}
