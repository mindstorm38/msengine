package io.msengine.common.asset;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

public abstract class Assets {
	
	public static Assets forClassLoader(ClassLoader loader, String root) {
		return new ClassLoaderAssets(loader, root);
	}
	
	public static Assets forClass(Class<?> clazz, String root) {
		return forClassLoader(clazz.getClassLoader(), root);
	}
	
	// Class //
	
	protected abstract Asset getAssetSimplified(String simplifiedPath);
	protected abstract InputStream openAssetStreamSimplified(String simplifiedPath);
	protected abstract List<Asset> listAssetsSimplified(String simplifiedPath, Predicate<String> filter);
	
	public Asset getAsset(String path) {
		return this.getAssetSimplified(simplifyPath(path));
	}
	
	public InputStream openAssetStream(String path) {
		return this.openAssetStreamSimplified(simplifyPath(path));
	}
	
	public List<Asset> listAssets(String path, Predicate<String> filter) {
		return this.listAssetsSimplified(simplifyPath(path), filter);
	}
	
	public List<Asset> listAssets(String path) {
		return this.listAssetsSimplified(simplifyPath(path), null);
	}
	
	public InputStream openAssetStreamExcept(String path) throws IOException {
		return throwIfNoStream(this.openAssetStream(path));
	}
	
	// Utils //
	
	public static String simplifyPath(String path) {
		StringBuilder builder = new StringBuilder();
		for (String part : path.split("/")) {
			if (!part.isEmpty()) {
				if (builder.length() != 0) {
					builder.append('/');
				}
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
	
	public static Predicate<String> getExtensionFilter(String ext) {
		return str -> str.endsWith(ext);
	}

}
