package io.msengine.example.gui;

import io.msengine.client.graphics.font.Font;
import io.msengine.client.graphics.font.FontFamily;
import io.msengine.client.graphics.font.GlyphPage;
import io.msengine.client.graphics.font.truetype.TrueTypeFontFamily;
import io.msengine.client.graphics.gui.GuiScene;
import io.msengine.client.graphics.gui.GuiTexture;
import io.msengine.client.graphics.gui.wrapper.GuiWrapperCentered;
import io.msengine.client.graphics.texture.ResTexture2D;
import io.msengine.client.graphics.texture.base.Texture2D;
import io.msengine.common.asset.Asset;
import io.msengine.common.asset.Assets;

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
	
	private final GuiTexture exampleTex;
	private ResTexture2D exampleTexObj;
	
	private final GuiTexture fontTex;
	private FontFamily fontFamily;
	private ResTexture2D fontTexObj;
	
	public GuiTestScene() {
		
		this.exampleTexBg = new GuiTexture();
		this.exampleTexBg.setPosition(0, 0);
		this.addChild(this.exampleTexBg);
		
		this.exampleTexBgCentered = new GuiWrapperCentered(this.exampleTexBg);
		
		this.exampleTex = new GuiTexture();
		this.exampleTex.setPosition(30, 30);
		this.exampleTex.setSize(200, 200);
		this.addChild(this.exampleTex);
		
		this.fontTex = new GuiTexture();
		this.fontTex.setPosition(300, 30);
		this.addChild(this.fontTex);
		
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
			
			this.fontFamily = new TrueTypeFontFamily(A_UBUNTU_FONT);
			System.out.println("Font family: " + this.fontFamily);
			Font font = this.fontFamily.getSize(26f);
			System.out.println("Font: " + font);
			
			GlyphPage page = font.getGlyphPage(30);
			System.out.println("Glyph page: " + page);
			
			/*GlyphPage page2 = font.getGlyphPage(160);
			System.out.println("Glyph page2: " + page2);
			
			GlyphPage page3 = font.getGlyphPage(90);
			System.out.println("Glyph page3: " + page3);*/
			
			this.fontTex.setTextureFull(page.getTexture());
			this.fontTex.setSize(page.getTexture().getWidth(), page.getTexture().getHeight());
			
			//this.fontTexObj = FontTest.test(16);
			//this.fontTex.setTextureFull(this.fontTexObj);
			//this.fontTex.setSize(this.fontTexObj.getWidth(), this.fontTexObj.getHeight());
			
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
		
		this.fontFamily.close();
		//this.fontTexObj.close();
		//this.fontTexObj = null;
		
	}
	
	@Override
	protected void onSceneResized(float width, float height) {
		super.onSceneResized(width, height);
		this.exampleTexBgCentered.setCenteredSize(0, 0, width, height);
	}
	
}
