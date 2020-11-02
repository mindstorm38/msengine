package io.msengine.common.asset.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import io.msengine.common.asset.Asset;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
	
	public JsonObject getJson(boolean refresh) {
		
		if (this.cachedJson == null || refresh) {
			try (Reader reader = new InputStreamReader(this.asset.openStreamExcept())) {
				this.cachedJson = GSON.fromJson(reader, JsonElement.class);
				if (this.cachedJson == null) {
					this.cachedJson = new MetadataParseException("Failed to parse json file, not an object.");
				}
			} catch (IOException | JsonSyntaxException e) {
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
		
		if (cached == null || refresh) {
			
			JsonObject json = this.getJson(refresh);
			
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
