package io.msengine.example.gui;

import io.msengine.client.graphics.gui.GuiColorSolid;
import io.msengine.client.graphics.gui.GuiParent;
import io.msengine.common.util.Color;

public class GuiRainbowFlag extends GuiParent {
	
	private final Color[] COLORS = {
			new Color(229, 0, 0),
			new Color(255, 141, 0),
			new Color(255, 238, 0),
			new Color(0, 129, 33),
			new Color(0, 76, 255),
			new Color(118, 1, 136)
	};
	
	private final GuiColorSolid[] colors = new GuiColorSolid[COLORS.length];
	
	public GuiRainbowFlag() {
		for (int i = 0; i < COLORS.length; ++i) {
			this.addChild(this.colors[i] = new GuiColorSolid(COLORS[i]));
		}
	}
	
	@Override
	protected void onWidthChanged(float width) {
		super.onWidthChanged(width);
		for (GuiColorSolid color : this.colors) {
			color.setWidth(width);
		}
	}
	
	@Override
	protected void onHeightChanged(float height) {
		super.onHeightChanged(height);
		float h = this.height / COLORS.length;
		for (int i = 0; i < COLORS.length; ++i) {
			this.colors[i].setPosition(0, i * h);
			this.colors[i].setSize(width, h);
		}
	}
	
}
