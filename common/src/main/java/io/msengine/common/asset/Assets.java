package io.msengine.common.asset;

import java.io.IOException;
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
	
	public InputStream openAssetStream(String path) {
		return this.openAssetStreamSimplified(simplifyPath(path));
	}
	
	protected abstract Asset getAssetSimplified(String simplifiedPath);
	protected abstract InputStream openAssetStreamSimplified(String simplifiedPath);
	
	public InputStream openAssetStreamExcept(String path) throws IOException {
		return throwIfNoStream(this.openAssetStream(path));
	}
	
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
	
	public static InputStream throwIfNoStream(InputStream stream) throws IOException {
		if (stream == null) {
			throw new IOException("No data stream for this asset.");
		} else {
			return stream;
		}
	}

}
