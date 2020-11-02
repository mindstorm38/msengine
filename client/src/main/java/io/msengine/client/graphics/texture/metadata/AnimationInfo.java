package io.msengine.client.graphics.texture.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.msengine.common.asset.metadata.MetadataParseException;
import io.msengine.common.asset.metadata.MetadataSection;
import io.msengine.common.util.JsonUtils;

public class AnimationInfo {
	
	public static final MetaSection META_SECTION = new MetaSection("animation");
	
	private final int framesCount;
	private final int tickSpeed;
	
	public AnimationInfo(int framesCount, int tickSpeed) {
		this.framesCount = framesCount;
		this.tickSpeed = tickSpeed;
	}
	
	public int getFramesCount() {
		return this.framesCount;
	}
	
	public int getTickSpeed() {
		return this.tickSpeed;
	}
	
	public static class MetaSection extends MetadataSection<AnimationInfo> {
		
		private MetaSection(String name) {
			super(name);
		}
		
		@Override
		public AnimationInfo parse(JsonElement raw) throws MetadataParseException {
			
			if (!raw.isJsonObject()) throw new MetadataParseException("The animation section must be an object.");
			JsonObject json = raw.getAsJsonObject();
			
			int framesCount = JsonUtils.getInt(json, "frames_count", 0);
			if (framesCount <= 0) throw new MetadataParseException("Invalid 'frames_count' <= 0 for animation section.");
			
			int tickSpeed = JsonUtils.getInt(json, "tick_speed", 0);
			if (tickSpeed <= 0) throw new MetadataParseException("Invalid 'tick_speed' <= 0 for animation section.");
			
			return new AnimationInfo(framesCount, tickSpeed);
			
		}
		
	}
	
}