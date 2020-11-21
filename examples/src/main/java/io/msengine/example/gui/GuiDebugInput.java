package io.msengine.example.gui;

import io.msengine.client.graphics.gui.GuiColorSolid;
import io.msengine.client.graphics.gui.GuiText;
import io.msengine.client.graphics.gui.GuiTextInput;
import io.msengine.common.util.Color;

public class GuiDebugInput extends GuiTextInput {

	private final GuiColorSolid bg = new GuiColorSolid(Color.BLACK);
	private final GuiColorSolid ref = new GuiColorSolid(Color.GREEN);
	private final GuiColorSolid txt = new GuiColorSolid(Color.BLUE);
	
	public GuiDebugInput() {
		
		super();
		
		this.addChild(this.bg, 0);
		
		this.txt.setAnchor(-1, 0);
		this.addChild(this.txt, 1);
		this.ref.setAnchor(-1, 0);
		this.ref.setHeight(1);
		this.ref.setXPos(0);
		this.addChild(this.ref, 2);
		
		this.txt.setVisible(false);
		this.ref.setVisible(false);
		
	}
	
	@Override
	protected void onTextSizeChanged() {
		super.onTextSizeChanged();
		GuiText text = this.getText();
		if (this.txt != null) {
			this.txt.setPos(0, text.getYOffsetFromParent());
			this.txt.setSize(this.realWidth, text.getRealHeight());
		}
	}
	
	@Override
	public void onRealWidthChanged() {
		super.onRealWidthChanged();
		this.bg.setWidth(this.realWidth);
		this.ref.setWidth(this.realWidth);
	}
	
	@Override
	public void onRealHeightChanged() {
		super.onRealHeightChanged();
		this.bg.setHeight(this.realHeight);
		this.ref.setYPos(this.realHeight / 2);
	}
	
}
