package msengine.common.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class JsonUtils {
	
	// Integer
	
	public static int getInt(JsonObject obj, String id, int def) {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isNumber() ) return p.getAsInt();
		}
		return def;
	}
	
	public static int getIntRequired(JsonObject obj, String id, String throwMessage) throws JsonParseException {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isNumber() ) return p.getAsInt();
		}
		throw new JsonParseException( throwMessage );
	}
	
	// Character
	
	public static char getChar(JsonObject obj, String id, char def) {
		if ( obj.has( id ) ) {
			try {
				return obj.getAsCharacter();
			} catch (Exception e) {}
		}
		return def;
	}
	
	public static char getCharRequired(JsonObject obj, String id, String throwMessage) throws JsonParseException {
		if ( obj.has( id ) ) {
			try {
				return obj.getAsCharacter();
			} catch (Exception e) {}
		}
		throw new JsonParseException( throwMessage );
	}
	
	// Long
	
	public static long getLong(JsonObject obj, String id, long def) {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isNumber() ) return p.getAsLong();
		}
		return def;
	}
	
	public static long getLongRequired(JsonObject obj, String id, String throwMessage) throws JsonParseException {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isNumber() ) return p.getAsLong();
		}
		throw new JsonParseException( throwMessage );
	}
	
	// Float

	public static float getFloat(JsonObject obj, String id, float def) {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isNumber() ) return p.getAsFloat();
		}
		return def;
	}
	
	public static float getFloatRequired(JsonObject obj, String id, String throwMessage) throws JsonParseException {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isNumber() ) return p.getAsFloat();
		}
		throw new JsonParseException( throwMessage );
	}
	
	// String
	
	public static String getString(JsonObject obj, String id, String def) {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isString() ) return p.getAsString();
		}
		return def;
	}
	
	public static String getStringRequired(JsonObject obj, String id, String throwMessage) throws JsonParseException {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isString() ) return p.getAsString();
		}
		throw new JsonParseException( throwMessage );
	}
	
	// Boolean
	
	public static boolean getBoolean(JsonObject obj, String id, boolean def) {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isBoolean() ) return p.getAsBoolean();
		}
		return def;
	}
	
	public static boolean getBooleanRequired(JsonObject obj, String id, String throwMessage) throws JsonParseException {
		if ( obj.has( id ) ) {
			JsonPrimitive p = obj.getAsJsonPrimitive( id );
			if ( p.isBoolean() ) return p.getAsBoolean();
		}
		throw new JsonParseException( throwMessage );
	}
	
}
