package io.msengine.example.gui;

import io.msengine.client.graphics.font.FontFamily;
import io.msengine.client.graphics.font.truetype.TrueTypeFontFamily;
import io.msengine.client.graphics.gui.GuiScene;
import io.msengine.client.graphics.gui.GuiScroll;
import io.msengine.client.graphics.gui.GuiText;
import io.msengine.client.graphics.gui.GuiTexture;
import io.msengine.client.graphics.gui.GuiTextureMosaic;
import io.msengine.client.graphics.gui.wrapper.GuiWrapperCentered;
import io.msengine.client.graphics.texture.ResTexture2D;
import io.msengine.client.graphics.texture.base.Texture2D;
import io.msengine.common.asset.Asset;
import io.msengine.common.asset.Assets;
import io.msengine.common.util.Color;

import java.io.IOException;

public class GuiTestScene extends GuiScene {
	
	private static final Assets ASSETS = Assets.forClass(GuiTestScene.class, "assets");
	private static final Asset A_EXAMPLE = ASSETS.getAsset("mseex/example.png");
	private static final Asset A_EXAMPLE_BG = ASSETS.getAsset("mseex/example_bg.jpg");
	private static final Asset A_UBUNTU_FONT = ASSETS.getAsset("mseex/Ubuntu-Regular.ttf");
	private static final Asset A_JETBRAINS_MONO = ASSETS.getAsset("mseex/JetBrainsMonoNL-Regular.ttf");
	
	private final GuiTexture exampleTexBg;
	private final GuiWrapperCentered exampleTexBgCentered;
	private ResTexture2D exampleTexBgObj;
	
	private final GuiScroll exampleScroll;
	private final GuiTexture exampleTex;
	private ResTexture2D exampleTexObj;
	
	
	private FontFamily jetbrainsFont;
	private FontFamily ubuntuFont;
	private final GuiText jetbrainsText;
	private final GuiText ubuntuText;
	private final GuiDebugInput testInput;
	
	public GuiTestScene() {
		
		this.exampleTexBg = new GuiTexture();
		this.exampleTexBg.setPos(0, 0);
		this.addChild(this.exampleTexBg);
		
		this.exampleTexBgCentered = new GuiWrapperCentered(this.exampleTexBg);
		
		this.exampleScroll = new GuiScroll();
		this.exampleScroll.setPos(30, 30);
		this.exampleScroll.setSize(150, 150);
		this.addChild(this.exampleScroll);
		
		this.exampleTex = new GuiTexture();
		this.exampleTex.setSize(200, 200);
		this.exampleScroll.getInternal().addChild(this.exampleTex);
		this.exampleScroll.resizeAutoInternal();
		
		this.jetbrainsText = new GuiText("while ((read = stream.read(buf)) != -1) {}");
		this.jetbrainsText.setPos(-20, -20);
		this.jetbrainsText.setSupAnchor(1, 1);
		this.jetbrainsText.setAnchor(1, 1);
		this.jetbrainsText.setIgnoreDescent(true);
		this.addChild(this.jetbrainsText);
		
		this.ubuntuText = new GuiText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus sed nisl commodo, porttitor libero ut, efficitur erat.");
		this.ubuntuText.setYPos(550);
		this.ubuntuText.setXAnchor(0);
		this.ubuntuText.setXSupAnchor(0);
		this.ubuntuText.addColorEffect(0, Color.GREEN);
		this.ubuntuText.addColorEffect(10, Color.RED);
		this.ubuntuText.addColorEffect(20, Color.BLUE);
		this.addChild(this.ubuntuText);
		
		this.testInput = new GuiDebugInput();
		this.testInput.setPos(30, 400);
		this.testInput.setSize(300, 30);
		this.testInput.setInputText("party");
		this.testInput.setPlaceholderText("placeholder");
		this.addChild(this.testInput);
		
	}
	
	@Override
	protected void init() {
		
		super.init();
		
		try {
			
			this.exampleTexBgObj = new ResTexture2D(A_EXAMPLE_BG);
			this.exampleTexBg.setTextureFull(this.exampleTexBgObj);
			this.exampleTexBgCentered.setGoalRatio((float) this.exampleTexBgObj.getWidth() / this.exampleTexBgObj.getHeight());
			
			this.exampleTexObj = new ResTexture2D(Texture2D.SETUP_NEAREST, A_EXAMPLE);
			this.exampleTex.setTextureFull(this.exampleTexObj);
			
			this.jetbrainsFont = new TrueTypeFontFamily(A_JETBRAINS_MONO);
			this.ubuntuFont = new TrueTypeFontFamily(A_UBUNTU_FONT);
			
			System.out.println("test: " + this.jetbrainsFont.getSize(16.6f).getAscent());
			
			this.jetbrainsText.setFont(this.jetbrainsFont, 16.6f);
			this.ubuntuText.setFont(this.ubuntuFont, 25);
			
			this.testInput.setTextFont(this.jetbrainsFont, 16.6f);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void stop() {
		
		super.stop();
		
		this.exampleTexBgObj.close();
		this.exampleTexBgObj = null;
		
		this.exampleTexObj.close();
		this.exampleTexObj = null;
		
		this.jetbrainsFont.close();
		this.jetbrainsFont = null;
		
		this.ubuntuFont.close();
		this.ubuntuFont = null;
		
	}
	
	@Override
	protected void onSceneResized(float width, float height) {
		super.onSceneResized(width, height);
		this.exampleTexBgCentered.setCenteredSize(0, 0, width, height);
		// this.ubuntuText.setXPos(width / 2);
		// this.jetbrainsText.setPos(width - 20, height - 20);
	}
	
}
