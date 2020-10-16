package io.msengine.common.asset;

import java.io.InputStream;

public abstract class Assets {
	
	public static Assets forClassLoader(ClassLoader loader, String root) {
		return new ClassLoaderAssets(loader, root);
	}
	
	public static Assets forClass(Class<?> clazz, String root) {
		return forClassLoader(clazz.getClassLoader(), root);
	}
	
	// Class //
	
	public Asset getAsset(String path) {
		return this.getAssetSimplified(simplifyPath(path));
	}
	
	protected abstract Asset getAssetSimplified(String simplifiedPath);
	public abstract InputStream openAssetStream(String path);
	
	// Utils //
	
	public static String simplifyPath(String path) {
		StringBuilder builder = new StringBuilder();
		for (String part : path.split("/")) {
			if (!part.isEmpty()) {
				builder.append('/');
				builder.append(part);
			}
		}
		return builder.toString();
	}

}
