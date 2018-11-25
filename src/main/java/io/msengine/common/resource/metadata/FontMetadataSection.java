package io.msengine.common.resource.metadata;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import io.msengine.common.util.JsonUtils;

public class FontMetadataSection implements MetadataSection {
	
	private final int height;
	private final Map<Character, FontMetadataGlyph> glyphs;
	
	public FontMetadataSection(int height, Map<Character, FontMetadataGlyph> glyphs) {
		
		this.height = height;
		this.glyphs = glyphs;
		
	}
	
	public int getHeight() { return this.height; }
	public Map<Character, FontMetadataGlyph> getGlyphs() { return this.glyphs; }
	
	public static class Serializer implements MetadataSectionSerializer<FontMetadataSection> {

		@Override
		public FontMetadataSection deserialize(JsonElement jsonRaw, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			JsonObject json = jsonRaw.getAsJsonObject();
			
			int height = JsonUtils.getIntRequired( json, "height", "Missing font height" );
			
			Map<Character, FontMetadataGlyph> glyphs = new HashMap<>();
			JsonArray glyphsArr = json.get("glyphs") != null && json.get("glyphs").isJsonArray() ? json.get("glyphs").getAsJsonArray() : new JsonArray();
			
			for ( JsonElement glyphObjRaw : glyphsArr ) {
				
				if ( !glyphObjRaw.isJsonObject() ) continue;
				
				JsonObject glyphObj = glyphObjRaw.getAsJsonObject();
				
				try {
					
					String charString = JsonUtils.getStringRequired( glyphObj, "char", null );
					
					int x = JsonUtils.getIntRequired( glyphObj, "x", null );
					int y = JsonUtils.getIntRequired( glyphObj, "y", null );
					int width = JsonUtils.getIntRequired( glyphObj, "width", null );
					
					if ( "unknown".equals( charString ) ) {
						
						glyphs.put( null, new FontMetadataGlyph( x, y, width ) );
						
					} else {
						
						char ch = charString.charAt(0);
						glyphs.put( ch, new FontMetadataGlyph( x, y, width ) );
						
					}
					
				} catch (Exception e) {
					continue;
				}
				
			}
			
			return new FontMetadataSection( height, glyphs );
			
		}

		@Override
		public JsonElement serialize(FontMetadataSection src, Type typeOfSrc, JsonSerializationContext context) {
			
			JsonObject json = new JsonObject();
			
			json.addProperty( "height", src.height );
			
			JsonArray glyphs = new JsonArray();
			json.add( "glyphs", glyphs );
			
			for ( Entry<Character, FontMetadataGlyph> glyphEntry : src.glyphs.entrySet() ) {
				
				JsonObject glyphObj = new JsonObject();
				glyphs.add( glyphObj );
				
				FontMetadataGlyph glyph = glyphEntry.getValue();
				
				glyphObj.addProperty( "char", glyphEntry.getKey() );
				glyphObj.addProperty( "x", glyph.x );
				glyphObj.addProperty( "y", glyph.y );
				glyphObj.addProperty( "width", glyph.width );
				
			}
			
			return json;
			
		}

		@Override
		public String getSectionIdentifier() {
			return "font";
		}
		
	}
	
}
