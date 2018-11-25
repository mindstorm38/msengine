package io.msengine.client.game;

import io.msengine.common.game.ServerGameOptions;

public class RenderGameOptions extends ServerGameOptions {

	private String initialWindowTitle;
	
	public RenderGameOptions(Class<?> runningClass) {
		
		super( runningClass );
		
		this.initialWindowTitle = "MSEngine Window";
		
	}
	
	public RenderGameOptions setInitialWindowTitle(String initialWindowTitle) {
		this.initialWindowTitle = initialWindowTitle;
		return this;
	}
	
	public String getInitialWindowTitle() {
		return this.initialWindowTitle;
	}
	
}
