package io.msengine.example.gui;

import io.msengine.client.graphics.gui.GuiColorSolid;
import io.msengine.client.graphics.gui.GuiParent;
import io.msengine.common.util.Color;

/**
 * A rainbow flag as an example for custom GUI parents.
 */
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
	public void onRealWidthChanged() {
		super.onRealWidthChanged();
		for (GuiColorSolid color : this.colors) {
			color.setWidth(this.realWidth);
		}
	}
	
	@Override
	public void onRealHeightChanged() {
		super.onRealHeightChanged();
		float h = this.realHeight / COLORS.length;
		for (int i = 0; i < COLORS.length; ++i) {
			this.colors[i].setPos(0, i * h);
			this.colors[i].setHeight(h);
		}
	}
	
}
