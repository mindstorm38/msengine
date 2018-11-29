package io.msengine.common.game;

import java.io.File;
import java.net.URISyntaxException;

public abstract class BaseGameOptions {
	
	private final Class<?> runningClass;
	private String resourceBaseFolderPath;
	private String loggerName;
	private File appdataDir;
	private String optionsFilePath;
	
	public BaseGameOptions(Class<?> runningClass) {
		
		this.runningClass = runningClass;
		this.resourceBaseFolderPath = "assets";
		this.loggerName = "MSEngine";
		
		try {
			
			this.appdataDir = new File( runningClass.getProtectionDomain().getCodeSource().getLocation().toURI() ).getParentFile();
			
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException( "Invalid running class, can't get source jar location", e );
		}
		
		this.optionsFilePath = "options.json";
		
	}
	
	public Class<?> getRunningClass() {
		return this.runningClass;
	}
	
	public void setResourceBaseFolderPath(String resourceBaseFolderPath) {
		this.resourceBaseFolderPath = resourceBaseFolderPath;
	}
	
	public String getResourceBaseFolderPath() {
		return this.resourceBaseFolderPath;
	}
	
	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}
	
	public String getLoggerName() {
		return this.loggerName;
	}
	
	public void setAppdataDir(File appdataDir) {
		this.appdataDir = appdataDir;
	}
	
	public File getAppdataDir() {
		return this.appdataDir;
	}
	
	public void setOptionsFile(String filePath) {
		this.optionsFilePath = filePath;
	}
	
	public String getOptionsFilePath() {
		return this.optionsFilePath;
	}
	
	public File getOptionsFile() {
		return new File( this.appdataDir, this.optionsFilePath );
	}
	
}
