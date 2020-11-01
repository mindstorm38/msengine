package io.msengine.common.asset.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import io.msengine.common.asset.Asset;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CachedMetadata implements Metadata {
	
	private static final Gson GSON = new GsonBuilder().create();
	private static final Object NO_SECTION_SENTINEL = new Object();
	
	private final Asset asset;
	private Map<MetadataSection<?>, Object> cachedSections;
	private Object cachedJson;
	
	public CachedMetadata(Asset asset) {
		this.asset = Objects.requireNonNull(asset);
	}
	
	public JsonObject getJson() {
		
		if (this.cachedJson == null) {
			try (JsonReader reader = new JsonReader(new InputStreamReader(this.asset.openStreamExcept()))) {
				this.cachedJson = GSON.fromJson(reader, JsonObject.class);
			} catch (IOException e) {
				this.cachedJson = new MetadataParseException("Failed to parse json file.", e);
			}
		}
		
		if (this.cachedJson.getClass() == MetadataParseException.class) {
			throw (MetadataParseException) this.cachedJson;
		} else {
			return (JsonObject) this.cachedJson;
		}
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getMetadataSection(MetadataSection<T> section, boolean refresh) throws MetadataParseException {
		
		if (this.cachedSections == null) {
			this.cachedSections = new HashMap<>();
		}
		
		Object cached = this.cachedSections.get(Objects.requireNonNull(section));
		
		if (cached == null) {
			
			JsonObject json = this.getJson();
			
			if (json.has(section.getName())) {
				try {
					cached = section.parse(json.get(section.getName()));
				} catch (MetadataParseException e) {
					cached = e;
				}
			}
			
			if (cached == null) {
				cached = NO_SECTION_SENTINEL;
			}
			
			this.cachedSections.put(section, cached);
			
		}
		
		if (cached == NO_SECTION_SENTINEL) {
			return null;
		} else if (cached.getClass() == MetadataParseException.class) {
			throw (MetadataParseException) cached;
		} else {
			return (T) cached;
		}
		
	}

}
