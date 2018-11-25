package msengine.common.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class OptionRange extends Option {
	
	private final int min;
	private final int max;
	
	private final int defaultValue;
	private int value;
	
	public OptionRange(String identifier, int min, int max, int defaultValue) {
		
		super(identifier);
		
		this.min = min;
		this.max = max;
		
		this.setValue( defaultValue );
		this.defaultValue = this.value;
		
	}
	
	public int getMin() { return this.min; }
	public int getMax() { return this.max; }
	
	public int getDefaultValue() { return this.defaultValue; }
	
	public int getValue() { return this.value; }
	public void setValue(int value) {
		if ( value < this.min || value > this.max ) return;
		this.value = value;
		this.newValue();
	}
	
	public static class Serializer implements OptionTypeAdapter<OptionRange> {

		@Override
		public JsonElement write(OptionRange obj) throws Exception {
			return new JsonPrimitive( obj.value );
		}

		@Override
		public void read(JsonElement json, OptionRange obj) throws Exception {
			
			try {
				
				int value = json.getAsJsonPrimitive().getAsInt();
				obj.setValue( value );
				
			} catch (Exception e) {}
			
		}
		
	}

}
