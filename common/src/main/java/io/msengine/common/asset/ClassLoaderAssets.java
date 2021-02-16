package io.msengine.common.asset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

final class ClassLoaderAssets extends Assets {
	
	private final ClassLoader loader;
	private final String root;
	
	public ClassLoaderAssets(ClassLoader loader, String root) {
		this.loader = Objects.requireNonNull(loader);
		this.root = simplifyPath(root);
	}
	
	private String getRootedPath(String path) {
		return this.root + '/' + path;
	}
	
	@Override
	protected Asset getAssetSimplified(String simplifiedPath) {
		if (this.loader.getResource(this.getRootedPath(simplifiedPath)) == null)
			return null;
		return new Asset(this, simplifiedPath);
	}
	
	@Override
	public InputStream openAssetStreamSimplified(String simplifiedPath) {
		return this.loader.getResourceAsStream(this.getRootedPath(simplifiedPath));
	}
	
	@Override
	protected List<Asset> listAssetsSimplified(String simplifiedPath, Predicate<String> filter) {
		
		InputStream stream = this.openAssetStreamSimplified(simplifiedPath);
		
		if (stream == null)
			return null;
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			
			List<Asset> assets = new ArrayList<>();
			String line;
			Asset asset;
			
			while ((line = reader.readLine()) != null) {
				if (filter == null || filter.test(line)) {
					asset = this.getAssetSimplified(simplifiedPath + '/' + Assets.simplifyPath(line));
					if (asset != null) {
						assets.add(asset);
					}
				}
			}
			
			return assets;
			
		} catch (IOException e) {
			return null;
		}
		
	}
	
}
