package io.msengine.example.gui;

import io.msengine.client.graphics.gui.GuiColorSolid;
import io.msengine.client.graphics.gui.GuiScene;

public class GuiTestScene extends GuiScene {

	private final GuiColorSolid color;
	
	public GuiTestScene() {
	
		this.color = new GuiColorSolid();
		this.color.setColor(200, 130, 240);
		this.color.setSize(100, 100);
		this.color.setPosition(100, 100);
		this.addChild(this.color);
		
	}

}
