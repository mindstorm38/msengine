package io.msengine.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathUtils {
	
	public static Path getAppDataPath() {
		
		String path, os = System.getProperty("os.name").toUpperCase();
		
		if (os.contains("WIN")) {
			path = System.getenv("APPDATA");
		} else if (os.contains("MAC")) {
			if ((path = System.getProperty("user.home")) != null) {
				return Paths.get(path, "Library", "Application Support");
			}
		} else if (os.contains("NUX")) {
			path = System.getProperty("user.home");
		} else {
			path = System.getProperty("user.dir");
		}
		
		if (path == null) {
			throw new UnsupportedOperationException("This runtime do not define an valid application data directory.");
		} else {
			return Paths.get(path);
		}
		
	}
	
	private PathUtils() { }
	
}
