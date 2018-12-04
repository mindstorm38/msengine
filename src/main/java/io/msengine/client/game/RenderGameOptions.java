package io.msengine.client.game;

import io.msengine.common.game.ServerGameOptions;

public class RenderGameOptions extends ServerGameOptions {

	private String baseLangsFolderPath;
	private String initialWindowTitle;
	private String defaultFontPath;
	
	public RenderGameOptions(Class<?> runningClass) {
		
		super( runningClass );
		
		this.baseLangsFolderPath = "langs";
		this.initialWindowTitle = "MSEngine Window";
		this.defaultFontPath = "textures/fonts/default.png";
		
	}
	
	public void setBaseLangsFolderPath(String baseLangsFolderPath) {
		this.baseLangsFolderPath = baseLangsFolderPath;
	}
	
	public String getBaseLangsFolderPath() {
		return this.baseLangsFolderPath;
	}
	
	public void setInitialWindowTitle(String initialWindowTitle) {
		this.initialWindowTitle = initialWindowTitle;
	}
	
	public String getInitialWindowTitle() {
		return this.initialWindowTitle;
	}
	
	public void setDefaultFontPath(String defaultFontPath) {
		this.defaultFontPath = defaultFontPath;
	}
	
	public String getDefaultFontPath() {
		return this.defaultFontPath;
	}
	
}
