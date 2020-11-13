package io.msengine.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class PathUtils {
	
	private static Set<String> RESERVED_FILENAMES;
	
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
	
	public static boolean isReservedPathName(String name) {
		if (RESERVED_FILENAMES == null) {
			RESERVED_FILENAMES = new HashSet<>(Arrays.asList(
					"COM", "PRN", "AUX", "NUL",
					"COM0", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
					"LPT0", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
			));
		}
		return RESERVED_FILENAMES.contains(name.toUpperCase());
	}
	
	private PathUtils() { }
	
}
