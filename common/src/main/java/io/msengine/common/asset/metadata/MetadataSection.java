package io.msengine.common.asset.metadata;

import com.google.gson.JsonElement;

import java.util.Objects;

public abstract class MetadataSection<T> {
	
	private final String name;
	
	public MetadataSection(String name) {
		this.name = Objects.requireNonNull(name);
	}
	
	public String getName() {
		return this.name;
	}
	
	public abstract T parse(JsonElement json) throws MetadataParseException;
	
	@Override
	public String toString() {
		return "MetadataSection<" + this.name + ">";
	}
	
}
