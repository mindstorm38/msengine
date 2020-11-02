package io.msengine.client.graphics.texture.metadata;

import com.google.gson.JsonElement;
import io.msengine.common.asset.metadata.MetadataParseException;
import io.msengine.common.asset.metadata.MetadataSection;

import java.util.Map;

public class PredefinedMap {
	
	public static final MetaSection META_SECTION = new MetaSection("map");
	
	private final int tilesWidth, tilesHeight;
	private Map<String, String> tileNameAliases;
	
	private PredefinedMap(int tilesWidth, int tilesHeight) {
		this.tilesWidth = tilesWidth;
		this.tilesHeight = tilesHeight;
	}
	
	public int getTilesWidth() {
		return this.tilesWidth;
	}
	
	public int getTilesHeight() {
		return this.tilesHeight;
	}
	
	public String getAlias(String raw) {
		return this.tileNameAliases == null ? raw : this.tileNameAliases.getOrDefault(raw, raw);
	}
	
	public static class MetaSection extends MetadataSection<PredefinedMap> {
		
		public MetaSection(String name) {
			super(name);
		}
		
		@Override
		public PredefinedMap parse(JsonElement json) throws MetadataParseException {
			return null;
		}
		
	}
	
}