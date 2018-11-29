package io.msengine.client.game;

import io.msengine.common.game.ServerGameOptions;

public class RenderGameOptions extends ServerGameOptions {

	private String baseLangsFolderPath;
	private String initialWindowTitle;
	
	public RenderGameOptions(Class<?> runningClass) {
		
		super( runningClass );
		

		this.baseLangsFolderPath = "langs";
		this.initialWindowTitle = "MSEngine Window";
		
	}
	
	public void setBaseLangsFolderPath(String baseLangsFolderPath) {
		this.baseLangsFolderPath = baseLangsFolderPath;
	}
	
	public String getBaseLangsFolderPath() {
		return this.baseLangsFolderPath;
	}
	
	public RenderGameOptions setInitialWindowTitle(String initialWindowTitle) {
		this.initialWindowTitle = initialWindowTitle;
		return this;
	}
	
	public String getInitialWindowTitle() {
		return this.initialWindowTitle;
	}
	
}
