package msengine.common.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class OptionBoolean extends Option {
	
	private final boolean defautValue;
	private boolean value;
	
	public OptionBoolean(String identifier, boolean defautValue) {
		
		super(identifier);
		
		this.defautValue = this.value = defautValue;
		
	}
	
	public boolean getDefaultValue() { return this.defautValue; } 
	
	public boolean getValue() { return this.value; }
	public void setValue(boolean value) {
		this.value = value;
		this.newValue();
	}

	public static class Serializer implements OptionTypeAdapter<OptionBoolean> {

		@Override
		public JsonElement write(OptionBoolean obj) throws Exception {
			return new JsonPrimitive( obj.value );
		}

		@Override
		public void read(JsonElement json, OptionBoolean obj) throws Exception {
			
			try {
				
				boolean value = json.getAsJsonPrimitive().getAsBoolean();
				obj.setValue( value );
				
			} catch (Exception e) {}
			
		}
		
	}
	
}
