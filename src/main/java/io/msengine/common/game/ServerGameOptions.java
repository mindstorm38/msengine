package io.msengine.common.game;

import java.io.File;
import java.net.URISyntaxException;

public class ServerGameOptions extends BaseGameOptions {
	
	private String resourceBaseFolderPath;
	private String baseLangsFolderPath;
	private String loggerName;
	private File appdataDir;
	private String optionsFilePath;
	
	public ServerGameOptions(Class<?> runningClass) {
		
		this.resourceBaseFolderPath = "assets";
		this.baseLangsFolderPath = "langs";
		this.loggerName = "MSEngine";
		
		try {
			
			this.appdataDir = new File( runningClass.getProtectionDomain().getCodeSource().getLocation().toURI() ).getParentFile();
			
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException( "Invalid running class, can't get source jar location", e );
		}
		
		this.optionsFilePath = "options.json";
		
	}
	
	public ServerGameOptions setResourceBaseFolderPath(String resourceBaseFolderPath) {
		this.resourceBaseFolderPath = resourceBaseFolderPath;
		return this;
	}
	
	public String getResourceBaseFolderPath() {
		return this.resourceBaseFolderPath;
	}
	
	public ServerGameOptions setBaseLangsFolderPath(String baseLangsFolderPath) {
		this.baseLangsFolderPath = baseLangsFolderPath;
		return this;
	}
	
	public String getBaseLangsFolderPath() {
		return this.baseLangsFolderPath;
	}
	
	public ServerGameOptions setLoggerName(String loggerName) {
		this.loggerName = loggerName;
		return this;
	}
	
	public String getLoggerName() {
		return this.loggerName;
	}
	
	public ServerGameOptions setAppdataDir(File appdataDir) {
		this.appdataDir = appdataDir;
		return this;
	}
	
	public File getAppdataDir() {
		return this.appdataDir;
	}
	
	public ServerGameOptions setOptionsFile(String filePath) {
		this.optionsFilePath = filePath;
		return this;
	}
	
	public String getOptionsFilePath() {
		return this.optionsFilePath;
	}
	
	public File getOptionsFile() {
		return new File( this.appdataDir, this.optionsFilePath );
	}
	
}
