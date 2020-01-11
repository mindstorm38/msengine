package io.msengine.common.resource.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import io.sutil.registry.NamespaceRegistry;

public class MetadataSerializer {
	
	// Singleton \\
	
	private static MetadataSerializer INSTANCE = null;
	
	public static MetadataSerializer getInstance() {
		if ( INSTANCE == null ) new MetadataSerializer();
		return INSTANCE;
	}
	
	// Class \\
	
	private final NamespaceRegistry<Class<? extends MetadataSection>> metadataSectionSerializerRegistry = new NamespaceRegistry<>();
	private final GsonBuilder builder;
	private Gson gson = null;
	
	private MetadataSerializer() {
		
		if ( INSTANCE != null ) throw new IllegalStateException();
		INSTANCE = this;
		
		this.builder = new GsonBuilder();
		
		// this.registerMetadataSectionType(new FontMetadataSection.Serializer(), FontMetadataSection.class);
		
	}
	
	public boolean hasMetadataSectionType(Class<? extends MetadataSection> sectionClass) {
		return this.metadataSectionSerializerRegistry.containsValue(sectionClass);
	}
	
	public <T extends MetadataSection> void registerMetadataSectionType(MetadataSectionSerializer<T> serializer, Class<T> clazz) {
		this.metadataSectionSerializerRegistry.register( serializer.getSectionIdentifier(), clazz );
		this.builder.registerTypeAdapter( clazz, serializer );
		this.gson = null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MetadataSection> T parseMetadataSection(String sectionIdentifier, JsonObject json) {
		
		if ( sectionIdentifier == null )
			throw new IllegalArgumentException("Metadata section identifier cannot be null");
		else if ( !json.has( sectionIdentifier ) )
			return null;
		else if ( !json.get( sectionIdentifier ).isJsonObject() )
			throw new IllegalArgumentException( "Invalid metadata section json type for '" + sectionIdentifier + "' expected object, found " + json.get( sectionIdentifier ).getClass().toString() );
		
		Class<? extends MetadataSection> clazz = this.metadataSectionSerializerRegistry.get( sectionIdentifier );
		
		if ( clazz == null ) throw new IllegalArgumentException( "Unhandled metadata section '" + sectionIdentifier + "'" );
		
		MetadataSection section = this.getGson().fromJson( json.getAsJsonObject( sectionIdentifier ), clazz );
		
		return (T) section;
		
	}
		
	public Gson getGson() {
		if ( this.gson == null ) this.gson = this.builder
				.setPrettyPrinting()
				.create();
		return this.gson;
	}
	
}
