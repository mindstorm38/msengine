package io.msengine.common.asset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Asset {
	
	private final Assets assets;
	private final String path;
	
	Asset(Assets assets, String path) {
		this.assets = assets;
		this.path = path;
	}
	
	/**
	 * Open an {@link InputStream} reading this asset data, <b>or null if this asset is no longer valid</b>.
	 * @return The data stream of null.
	 */
	public InputStream openStream() {
		return this.assets.openAssetStreamSimplified(this.path);
	}
	
	/*public InputStream openStreamExcept() throws IOException {
		InputStream stream = this.openStream();
		if (stream == null) {
			throw new IOException("");
		}
	}*/
	
	/**
	 * Get sub assets <b>only if this asset is a directory</b>.
	 * @param filter An optional filter to apply to assets list against there path.
	 * @return Assets list, <b>or null if this asset is no longer valid <i>(not returning an InputStream, this should not happen)</i></b>.
	 */
	public List<Asset> getAssets(Predicate<String> filter) {
		
		InputStream stream = this.openStream();
		
		if (stream == null)
			return null;
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.openStream()))) {
			
			List<Asset> assets = new ArrayList<>();
			String line;
			Asset asset;
			
			while ((line = reader.readLine()) != null) {
				if (filter == null || filter.test(line)) {
					asset = this.assets.getAsset(this.path + Assets.simplifyPath(line));
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
	
	public List<Asset> getAssets() {
		return this.getAssets(null);
	}
	
	public static Predicate<String> getExtensionFilter(String ext) {
		return str -> str.endsWith(ext);
	}
	
}
