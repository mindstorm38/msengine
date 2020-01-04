package io.msengine.common.resource.metadata;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface MetadataSectionSerializer<T extends MetadataSection> extends JsonDeserializer<T>, JsonSerializer<T> {
	
	String getSectionIdentifier();
	
}
