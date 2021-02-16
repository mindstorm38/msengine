package io.msengine.client.graphics.texture.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.msengine.common.asset.metadata.MetadataParseException;
import io.msengine.common.asset.metadata.MetadataSection;
import io.msengine.common.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class PredefinedMap {
	
	public static final MetaSection META_SECTION = new MetaSection("map");
	
	private final int tileWidth, tileHeight;
	private Map<String, String> tileNameAliases;
	
	private PredefinedMap(int tileWidth, int tileHeight) {
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
	public int getTileWidth() {
		return this.tileWidth;
	}
	
	public int getTileHeight() {
		return this.tileHeight;
	}
	
	public String getAlias(String raw) {
		return this.tileNameAliases == null ? null : this.tileNameAliases.get(raw);
	}
	
	public static class MetaSection extends MetadataSection<PredefinedMap> {
		
		public MetaSection(String name) {
			super(name);
		}
		
		@Override
		public PredefinedMap parse(JsonElement raw) throws MetadataParseException {
			
			if (!raw.isJsonObject()) throw new MetadataParseException("The predefined map section must be an object.");
			JsonObject json = raw.getAsJsonObject();
			
			int tileWidth = JsonUtils.getInt(json, "tile_width", 0);
			if (tileWidth <= 0) throw new MetadataParseException("Predefined map section requires a 'tile_width' integer.");
			
			int tileHeight = JsonUtils.getInt(json, "tile_height", 0);
			if (tileHeight <= 0) throw new MetadataParseException("Predefined map section requires a 'tile_height' integer.");
			
			PredefinedMap map = new PredefinedMap(tileWidth, tileHeight);
			
			if (json.has("aliases")) {
				
				JsonElement aliasesRaw = json.get("aliases");
				if (!aliasesRaw.isJsonObject()) throw new MetadataParseException("Predefined map section define an 'aliases' section, but it must be an object.");
				
				JsonObject aliases = aliasesRaw.getAsJsonObject();
				if (aliases.size() != 0) {
					map.tileNameAliases = new HashMap<>();
					for (Map.Entry<String, JsonElement> aliasRaw : aliasesRaw.getAsJsonObject().entrySet()) {
						if (!aliasRaw.getValue().isJsonPrimitive()) {
							throw new MetadataParseException("Predefined map section alias '" + aliasRaw.getKey() + "' must be a string.");
						}
						map.tileNameAliases.put(aliasRaw.getKey(), aliasRaw.getValue().getAsString());
					}
				}
				
			}
			
			return map;
			
		}
		
	}
	
}