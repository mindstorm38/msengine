package io.msengine.common.resource.metadata;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.sutil.StreamUtils;

import io.msengine.common.resource.DetailledResource;
import static io.msengine.common.util.GameLogger.LOGGER;

@Deprecated
public class Metadata {
	
	private final MetadataSerializer serializer;
	private final DetailledResource resource;
	private final Map<String, MetadataSection> sections;
	private JsonObject json;
	
	public Metadata(DetailledResource resource) {
		
		this.serializer = MetadataSerializer.getInstance();
		this.resource = resource;
		this.sections = new HashMap<>();
		
	}
	
	public boolean valid() {
		return this.resource.getMetadataInputStream() != null;
	}
	
	public void updateJson() {
		
		if ( !this.valid() ) return;
		
		Reader reader = null;
		
		try {
			
			reader = new InputStreamReader( this.resource.getMetadataInputStream() );
			this.json = ( new JsonParser() ).parse( reader ).getAsJsonObject();
			
		} catch (Exception e) {
			LOGGER.log( Level.SEVERE, null, e );
		} finally {
			StreamUtils.safeclose( reader );
		}
		
	}
	
	public <A extends MetadataSection> A getMetadataSection(String section) {
		return this.getMetadataSection( section, false );
	}
	
	@SuppressWarnings("unchecked")
	public <A extends MetadataSection> A getMetadataSection(String section, boolean refresh) {
		
		if ( !this.valid() ) return null;
		
		if ( this.json == null ) this.updateJson();
		
		A ret = (A) this.sections.get( section );
		
		if ( refresh || ret == null ) {
			
			try {
				
				ret = this.serializer.parseMetadataSection( section, this.json );
				
				if ( ret != null )
					this.sections.put( section, ret );
				
			} catch (Exception ignored) {}
			
		}
		
		return ret;
		
	}
	
}
