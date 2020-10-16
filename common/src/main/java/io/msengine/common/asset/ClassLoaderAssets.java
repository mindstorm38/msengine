package io.msengine.common.asset;

import java.io.InputStream;
import java.util.Objects;

final class ClassLoaderAssets extends Assets {
	
	private final ClassLoader loader;
	private final String root;
	
	public ClassLoaderAssets(ClassLoader loader, String root) {
		this.loader = Objects.requireNonNull(loader);
		this.root = simplifyPath(root);
	}
	
	@Override
	protected Asset getAssetSimplified(String simplifiedPath) {
		String fullPath = this.root + simplifiedPath;
		if (this.loader.getResource(fullPath) == null)
			return null;
		return new Asset(this, fullPath);
	}
	
	@Override
	public InputStream openAssetStream(String path) {
		return this.loader.getResourceAsStream(path);
	}
	
}
