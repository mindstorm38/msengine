package io.msengine.client.game;

import io.msengine.common.game.ServerGameOptions;

import java.io.File;
import java.util.Objects;

@Deprecated
public class RenderGameOptions extends ServerGameOptions {

	private String baseLangsFolderPath;
	private String initialWindowTitle;
	private String defaultFontPath;
	private String optionsFilePath;
	
	public RenderGameOptions(Class<?> runningClass) {
		
		super( runningClass );
		
		this.baseLangsFolderPath = "langs";
		this.initialWindowTitle = "MSEngine Window";
		this.defaultFontPath = "textures/fonts/default.png";
		this.optionsFilePath = "options.json";
		
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
	
	public void setOptionsFile(String filePath) {
		this.optionsFilePath = Objects.requireNonNull(filePath);
	}
	
	public String getOptionsFilePath() {
		return this.optionsFilePath;
	}
	
	public File getOptionsFile() {
		return new File( this.getAppdataDir(), this.optionsFilePath );
	}
	
}
