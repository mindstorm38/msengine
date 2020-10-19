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
		if (this.loader.getResource(this.root + simplifiedPath) == null)
			return null;
		return new Asset(this, simplifiedPath);
	}
	
	@Override
	public InputStream openAssetStreamSimplified(String simplifiedPath) {
		return this.loader.getResourceAsStream(this.root + simplifiedPath);
	}
	
}
