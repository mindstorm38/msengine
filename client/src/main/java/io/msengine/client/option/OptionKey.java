package io.msengine.client.option;

import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.msengine.common.util.JsonUtils;

@Deprecated
public class OptionKey extends Option {
	
	private final int defaultKeyCode;
	private int keyCode;
	
	private final boolean defaultKeyCtrl;
	private boolean keyCtrl;
	
	private final boolean defaultKeyAlt;
	private boolean keyAlt;
	
	private final boolean defaultKeyShift;
	private boolean keyShift;
	
	public OptionKey(String identifier, int defaultKeyCode, boolean defaultKeyCtrl, boolean defaultKeyAlt, boolean defaultKeyShift) {
		
		super(identifier);
		
		this.defaultKeyCode = this.keyCode = defaultKeyCode;
		this.defaultKeyCtrl = this.keyCtrl = defaultKeyCtrl;
		this.defaultKeyAlt = this.keyAlt = defaultKeyAlt;
		this.defaultKeyShift = this.keyShift = defaultKeyShift;
		
	}
	
	public OptionKey(String identifier, int defaultKeyCode) {
		this( identifier, defaultKeyCode, false, false, false );
	}
	
	public int getDefaultKeyCode() { return this.defaultKeyCode; }
	public boolean getDefaultKeyCtrl() { return this.defaultKeyCtrl; }
	public boolean getDefaultKeyAlt() { return this.defaultKeyAlt; }
	public boolean getDefaultKeyShift() { return this.defaultKeyShift; }
	
	public int getKeyCode() { return this.keyCode; }
	public void setKeyCode(int keyCode) { this.keyCode = keyCode; }
	
	public boolean getKeyCtrl() { return this.keyCtrl; }
	public void setKeyCtrl(boolean keyCtrl) { this.keyCtrl = keyCtrl; }
	
	public boolean getKeyAlt() { return this.keyAlt; }
	public void setKeyAlt(boolean keyAlt) { this.keyAlt = keyAlt; }
	
	public boolean getKeyShift() { return this.keyShift; }
	public void setKeyShift(boolean keyShift) { this.keyShift = keyShift; }
	
	public boolean isValid(int key, int scancode, int mods) {
		
		if ( this.keyCode != key ) return false;
		if ( this.keyCtrl && ( mods & GLFW.GLFW_MOD_CONTROL ) == 0 ) return false;
		if ( this.keyShift && ( mods & GLFW.GLFW_MOD_SHIFT ) == 0 ) return false;
		if ( this.keyAlt && ( mods & GLFW.GLFW_MOD_ALT ) == 0 ) return false;
		
		return true;
		
	}
	
	public static class Serializer implements OptionTypeAdapter<OptionKey> {

		@Override
		public JsonElement write(OptionKey obj) throws Exception {

			JsonObject json = new JsonObject();
			
			json.addProperty( "code", obj.keyCode );
			json.addProperty( "ctrl", obj.keyCtrl );
			json.addProperty( "alt", obj.keyAlt );
			json.addProperty( "shift", obj.keyShift );
			
			return json;
			
		}

		@Override
		public void read(JsonElement rawJson, OptionKey obj) throws Exception {
			
			JsonObject json = rawJson.getAsJsonObject();
			
			int keyCode = JsonUtils.getInt( json, "code", obj.defaultKeyCode );
			boolean keyCtrl = JsonUtils.getBoolean( json, "ctrl", obj.defaultKeyCtrl );
			boolean keyAlt = JsonUtils.getBoolean( json, "alt", obj.defaultKeyAlt );
			boolean keyShift = JsonUtils.getBoolean( json, "shift", obj.defaultKeyShift );
			
			obj.setKeyCode( keyCode );
			obj.setKeyCtrl( keyCtrl );
			obj.setKeyAlt( keyAlt );
			obj.setKeyShift( keyShift );
			
		}
		
	}
	
}
