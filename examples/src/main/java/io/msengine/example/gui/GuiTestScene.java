package io.msengine.example.gui;

import io.msengine.client.graphics.gui.GuiScene;

public class GuiTestScene extends GuiScene {
	
	public GuiTestScene() {
		
		GuiRainbowFlag flag = new GuiRainbowFlag();
		flag.setPosition(100, 100);
		flag.setSize(300, 200);
		this.addChild(flag);
		
		GuiRainbowFlag flag2 = new GuiRainbowFlag();
		flag2.setPosition(400, 600);
		flag2.setSize(200, 100);
		this.addChild(flag2);
		
	}
	
}
