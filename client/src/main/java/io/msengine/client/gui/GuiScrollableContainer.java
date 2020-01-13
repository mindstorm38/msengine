package io.msengine.client.gui;

import io.msengine.client.renderer.gui.GUIMaskRectangle;
import io.msengine.client.renderer.gui.GuiMask;

public class GuiScrollableContainer extends GuiParent {

	private final InternalContainer internal;
	private final GUIMaskRectangle mask;
	private final GuiMask[] maskArray;
	
	private float xRatio = 0;
	private float yRatio = 0;
	
	private float xMaxScroll = 0;
	private float yMaxScroll = 0;
	
	public GuiScrollableContainer() {
		
		this.internal = new InternalContainer();
		this.addChild(this.internal);
		
		this.mask = new GUIMaskRectangle();
		this.maskArray = new GuiMask[] { this.mask };
		
	}
	
	// Overrides //
	
	@Override
	public void init() {
		super.init();
		this.mask.init();
	}
	
	@Override
	public void stop() {
		this.mask.stop();
		super.stop();
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.updateXInfo();
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.updateYInfo();
	}
	
	@Override
	public void render(float alpha) {
		
		this.renderer.mask(this.maskArray);
		super.render(alpha);
		this.renderer.unmask();
		
	}
	
	@Override
	public void updateXOffset() {
		
		super.updateXOffset();
		this.mask.setX(this.xOffset);
		this.mask.setWidth(this.width);
		
	}
	
	@Override
	public void updateYOffset() {
		
		super.updateYOffset();
		this.mask.setY(this.yOffset);
		this.mask.setHeight(this.height);
		
	}
	
	// Scroll //
	public float getXMaxScroll() {
		return this.xMaxScroll;
	}
	
	public float getYMaxScroll() {
		return this.yMaxScroll;
	}
	
	// Update ratio //
	private void updateXInfo() {
		
		this.xRatio = this.internal.getWidth() / this.width;
		this.xMaxScroll = Math.max(this.internal.getWidth() - this.width, 0);
		
		if (this.internal.getXPos() > this.xMaxScroll)
			this.internal.setXPos(this.xMaxScroll);
		
	}
	
	private void updateYInfo() {
		
		this.yRatio = this.internal.getHeight() / this.height;
		this.yMaxScroll = Math.max(this.internal.getHeight() - this.height, 0);
		
		if (this.internal.getYPos() > this.yMaxScroll)
			this.internal.setYPos(this.yMaxScroll);
		
	}
	
	// Internal updated //
	private void internalWidthUpdated() {
		this.updateXInfo();
	}
	
	private void internalHeightUpdated() {
		this.updateYInfo();
	}
	
	public GuiParent getInternal() {
		return this.internal;
	}
	
	private class InternalContainer extends GuiParent {
		
		@Override
		public void setWidth(float width) {
			super.setWidth(width);
			GuiScrollableContainer.this.internalWidthUpdated();
		}
		
		@Override
		public void setHeight(float height) {
			super.setHeight(height);
			GuiScrollableContainer.this.internalHeightUpdated();
		}
		
		@Override
		public void childXOffsetUpdated(GuiObject child) {
			
			super.childXOffsetUpdated(child);
			
			float childOposOff = child.getOpositeOffsetX();
			
			if (childOposOff > this.getOpositeOffsetX())
				this.setWidth(childOposOff - this.width);
			
		}
		
		@Override
		public void childYOffsetUpdated(GuiObject child) {
			
			super.childYOffsetUpdated(child);
			
			float childOposOff = this.getOpositeOffsetY();
			
			if (childOposOff > this.getOpositeOffsetY())
				this.setHeight(childOposOff - this.height);
			
		}
		
	}
	
}
