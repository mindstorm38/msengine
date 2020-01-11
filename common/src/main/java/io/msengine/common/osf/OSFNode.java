package io.msengine.common.osf;

/**
 * OSF (Object Serialization Format) is a save format for many use. Look like JSON structure
 */
public abstract class OSFNode {
	
	protected OSFNode parent = null;
	
	public OSFNode getParent() {
		return this.parent;
	}
	
	public boolean isArray() { return false; }
	public boolean isObject() { return false; }
	public boolean isNumber() { return false; }
	public boolean isBoolean() { return false; }
	public boolean isText() { return false; }
	
	public OSFArray getAsArray() { return null; }
	public OSFObject getAsObject() { return null; }
	public OSFNumber getAsNumber() { return null; }
	public OSFBoolean getAsBoolean() { return null; }
	public OSFText getAsText() { return null; }
	
	public abstract OSFNode copy();
	
}
