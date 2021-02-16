package io.msengine.client.option;

/**
 * 
 * Game option class
 * Used to save game options
 * 
 * @author Mindstorm38
 *
 */
@Deprecated
public abstract class Option {
	
	protected final String identifier;
	
	public Option(String identifier) {
		
		this.identifier = identifier;
		
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public int hashCode() {
		return this.identifier.hashCode();
	}
	
	public void newValue() {}
	
}
