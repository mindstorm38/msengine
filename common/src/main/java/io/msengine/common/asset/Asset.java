package io.msengine.common.asset;

import io.msengine.common.asset.metadata.CachedMetadata;
import io.msengine.common.asset.metadata.Metadata;
import io.msengine.common.asset.metadata.MetadataParseException;
import io.msengine.common.asset.metadata.MetadataSection;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

public class Asset {
	
	private final Assets assets;
	private final String path;
	private Metadata meta;
	
	Asset(Assets assets, String path) {
		this.assets = assets;
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Open an {@link InputStream} reading this asset data, <b>or null if this asset is no longer valid</b>.
	 * @return The data stream of null.
	 * @see Assets#openAssetStreamSimplified(String)
	 */
	public InputStream openStream() {
		return this.assets.openAssetStreamSimplified(this.path);
	}

	/**
	 * Open an {@link InputStream} reading this asset data, or throw an {@link IOException}.
	 * @return The data stream.
	 * @throws IOException If the stream can't be opened.
	 * @see Assets#throwIfNoStream(InputStream)
	 * @see #openStream()
	 */
	public InputStream openStreamExcept() throws IOException {
		return Assets.throwIfNoStream(this.openStream());
	}
	
	/**
	 * Get sub assets <b>only if this asset is a directory</b>.
	 * @param filter An optional filter to apply to assets list against there path.
	 * @return Assets list, <b>or null if this asset is no longer valid <i>(not returning an InputStream, this should not happen)</i></b>.
	 */
	public List<Asset> listAssets(Predicate<String> filter) {
		return this.assets.listAssetsSimplified(this.path, filter);
	}
	
	public List<Asset> listAssets() {
		return this.listAssets(null);
	}
	
	// Metadata //
	
	public Metadata getMetadata() {
		if (this.meta == null) {
			Asset metadataAsset = this.assets.getAssetSimplified(this.path + ".meta");
			this.meta = (metadataAsset == null) ? Metadata.NO_META : new CachedMetadata(metadataAsset);
		}
		return this.meta;
	}
	
	public <T> T getMetadataSection(MetadataSection<T> section) throws MetadataParseException {
		return this.getMetadata().getMetadataSection(section);
	}
	
	// To String //
	
	@Override
	public String toString() {
		return "Asset<path=" + this.path + ">";
	}
	
}
