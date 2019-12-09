package io.msengine.client.renderer.texture.metadata;

import com.google.gson.*;
import io.msengine.common.resource.metadata.MetadataSection;
import io.msengine.common.resource.metadata.MetadataSectionSerializer;
import io.msengine.common.util.JsonUtils;

import java.lang.reflect.Type;

public class AnimationMetadataSection implements MetadataSection {

	private final int framesCount;
	private final int tickSpeed;
	
	public AnimationMetadataSection(int framesCount, int tickSpeed) {
		
		this.framesCount = framesCount;
		this.tickSpeed = tickSpeed;
		
	}
	
	public int getFramesCount() {
		return this.framesCount;
	}
	
	public int getTickSpeed() {
		return this.tickSpeed;
	}
	
	public static class Serializer implements MetadataSectionSerializer<AnimationMetadataSection> {
		
		@Override
		public String getSectionIdentifier() {
			return "animation";
		}
		
		@Override
		public AnimationMetadataSection deserialize(JsonElement jsonRaw, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			JsonObject json = jsonRaw.getAsJsonObject();
			
			int framesCount = JsonUtils.getIntRequired(json, "frames_count", "Missing frames count");
			
			if (framesCount <= 0)
				throw new JsonParseException("Invalid frames count <= 0");
			
			int tickSpeed = JsonUtils.getIntRequired(json, "tick_speed", "Missing tick speed");
			
			return new AnimationMetadataSection(framesCount, tickSpeed);
			
		}
		
		@Override
		public JsonElement serialize(AnimationMetadataSection src, Type typeOfSrc, JsonSerializationContext context) {
			
			JsonObject json = new JsonObject();
			json.addProperty("frames_count", src.getFramesCount());
			json.addProperty("tick_speed", src.getTickSpeed());
			return json;
			
		}
		
	}

}
