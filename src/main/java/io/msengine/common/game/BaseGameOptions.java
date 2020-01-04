package io.msengine.common.game;

import io.sutil.StringUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Base class for game options, contains :
 * <ul>
 *     <li>Base folder for for resources (default to 'assets')</li>
 *     <li>Logger name (default to 'MSEngine')</li>
 *     <li>Appdata directory (default to folder where the current JAR is running)</li>
 *     <li>Options file path from appdata directory (default to 'options.json')</li>
 *     <li>Resource namespace for the game (default to 'root')</li>
 * </ul>
 */
public abstract class BaseGameOptions {
	
	private final Class<?> runningClass;
	private String resourceBaseFolderPath;
	private String loggerName;
	private File appdataDir;
	private String optionsFilePath;
	private String resourceNamespace;
	
	public BaseGameOptions(Class<?> runningClass) {
		
		this.runningClass = runningClass;
		this.resourceBaseFolderPath = "assets";
		this.loggerName = "MSEngine";
		this.resourceNamespace = "root";
		
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
		this.loggerName = Objects.requireNonNull(loggerName);
	}
	
	public String getLoggerName() {
		return this.loggerName;
	}
	
	public void setAppdataDir(File appdataDir) {
		this.appdataDir = Objects.requireNonNull(appdataDir);
	}
	
	public File getAppdataDir() {
		return this.appdataDir;
	}
	
	public void setOptionsFile(String filePath) {
		this.optionsFilePath = Objects.requireNonNull(filePath);
	}
	
	public String getOptionsFilePath() {
		return this.optionsFilePath;
	}
	
	public File getOptionsFile() {
		return new File( this.appdataDir, this.optionsFilePath );
	}
	
	public String getResourceNamespace() {
		return this.resourceNamespace;
	}
	
	public void setResourceNamespace(String namespace) {
		this.resourceNamespace = StringUtils.requireNonNullAndEmpty(namespace);
	}
	
}
