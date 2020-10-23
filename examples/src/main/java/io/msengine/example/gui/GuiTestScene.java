package io.msengine.example.gui;

import io.msengine.client.graphics.gui.GuiScene;
import io.msengine.client.graphics.gui.GuiTexture;
import io.msengine.client.graphics.texture.ResTexture;
import io.msengine.common.asset.Asset;
import io.msengine.common.asset.Assets;

import java.io.IOException;

public class GuiTestScene extends GuiScene {
	
	private static final Assets ASSETS = Assets.forClass(GuiTestScene.class, "assets");
	private static final Asset A_EXAMPLE = ASSETS.getAsset("mseex/example.png");
	
	private final GuiTexture tex;
	private ResTexture resTex;
	
	public GuiTestScene() {
		
		this.tex = new GuiTexture();
		this.tex.setPosition(30, 30);
		this.tex.setSize(100, 100);
		this.addChild(this.tex);
		
		/*GuiRainbowFlag flag = new GuiRainbowFlag();
		flag.setPosition(30, 100);
		flag.setSize(400, 200);
		this.addChild(flag);*/
		
	}
	
	@Override
	protected void init() {
		
		super.init();
		
		try {
			this.tex.setTextureFull(this.resTex = new ResTexture(A_EXAMPLE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void stop() {
		
		super.stop();
		
		this.resTex.close();
		this.resTex = null;
		
	}
	
}
