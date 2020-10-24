package io.msengine.example.gui;

import io.msengine.client.graphics.gui.GuiScene;
import io.msengine.client.graphics.gui.GuiTexture;
import io.msengine.client.graphics.texture.DynTexture2D;
import io.msengine.client.graphics.texture.base.Texture2D;
import io.msengine.common.asset.Asset;
import io.msengine.common.asset.Assets;

import java.io.IOException;

public class GuiTestScene extends GuiScene {
	
	private static final Assets ASSETS = Assets.forClass(GuiTestScene.class, "assets");
	private static final Asset A_EXAMPLE = ASSETS.getAsset("mseex/example.png");
	
	private final GuiTexture tex;
	private Texture2D resTex;
	
	public GuiTestScene() {
		
		this.tex = new GuiTexture();
		this.tex.setPosition(30, 30);
		this.tex.setSize(200, 200);
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
			
			this.resTex = new DynTexture2D(Texture2D.SETUP_NEAREST, A_EXAMPLE, true);
			this.tex.setTextureFull(this.resTex);
			
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
