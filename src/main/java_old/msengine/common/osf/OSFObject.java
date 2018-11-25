package msengine.common.osf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

public class OSFObject extends OSFNode {
	
	private final Map<String, OSFNode> children = new HashMap<>();
	
	public OSFObject() {
		
	}
	
	public Map<String, OSFNode> getChildren() {
		return Collections.unmodifiableMap( this.children );
	}
	
	public boolean has(String key) {
		return this.children.containsKey( key );
	}
	
	public OSFNode get(String key) {
		return this.children.get( key );
	}
	
	private void throwInvalidKey(String key, String type) {
		 throw new IllegalArgumentException( "Invalid children key '" + key + "' for an " + type );
	}
	
	public OSFArray getArrayOrNull(String key) {
		OSFNode child = this.children.get( key );
		return child == null ? null : child.getAsArray();
	}
	
	public OSFObject getObjectOrNull(String key) {
		OSFNode child = this.children.get( key );
		return child == null ? null : child.getAsObject();
	}
	
	public byte getByte(String key, byte def) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) return def;
		return child.getAsNumber().getAsByte();
	}
	
	public byte getByteRequired(String key) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) this.throwInvalidKey( key, "byte" );
		return child.getAsNumber().getAsByte();
	}
	
	public short getShort(String key, short def) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) return def;
		return child.getAsNumber().getAsShort();
	}
	
	public short getShortRequired(String key) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) this.throwInvalidKey( key, "short" );
		return child.getAsNumber().getAsShort();
	}
	
	public int getInt(String key, int def) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) return def;
		return child.getAsNumber().getAsInteger();
	}
	
	public int getIntRequired(String key) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) this.throwInvalidKey( key, "integer" );
		return child.getAsNumber().getAsInteger();
	}
	
	public long getLong(String key, long def) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) return def;
		return child.getAsNumber().getAsLong();
	}
	
	public long getLongRequired(String key) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) this.throwInvalidKey( key, "long" );
		return child.getAsNumber().getAsLong();
	}
	
	public float getFloat(String key, float def) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) return def;
		return child.getAsNumber().getAsFloat();
	}
	
	public float getFloatRequired(String key) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isNumber() ) this.throwInvalidKey( key, "float" );
		return child.getAsNumber().getAsFloat();
	}
	
	public String getString(String key, String def) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isText() ) return def;
		return child.getAsText().getText();
	}
	
	public String getStringRequired(String key) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isText() ) this.throwInvalidKey( key, "string" );
		return child.getAsText().getText();
	}
	
	public boolean getBoolean(String key, boolean def) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isBoolean() ) return def;
		return child.getAsBoolean().getBoolean();
	}
	
	public boolean getBooleanRequired(String key) {
		OSFNode child = this.children.get( key );
		if ( child == null || !child.isBoolean() ) this.throwInvalidKey( key, "boolean" );
		return child.getAsBoolean().getBoolean();
	}
	
	public int size() {
		return this.children.size();
	}
	
	public void set(String key, OSFNode node) {
		if ( node == this ) return;
		this.children.put( key, node );
	}
	
	public void setBoolean(String key, boolean value) {
		this.set( key, new OSFBoolean( value ) );
	}
	
	public void setByte(String key, byte value) {
		this.set( key, new OSFNumber( value ) );
	}
	
	public void setShort(String key, short value) {
		this.set( key, new OSFNumber( value ) );
	}
	
	public void setInt(String key, int value) {
		this.set( key, new OSFNumber( value ) );
	}
	
	public void setLong(String key, long value) {
		this.set( key, new OSFNumber( value ) );
	}
	
	public void setFloat(String key, float value) {
		this.set( key, new OSFNumber( value ) );
	}
	
	public void setString(String key, String value) {
		this.set( key, new OSFText( value ) );
	}
	
	public void forEach(BiConsumer<? super String, ? super OSFNode> action) {
		this.children.forEach( action );
	}
	
	@Override
	public boolean isObject() {
		return true;
	}
	
	@Override
	public OSFObject getAsObject() {
		return this;
	}

	@Override
	public OSFNode copy() {
		OSFObject copy = new OSFObject();
		for ( Entry<String, OSFNode> childEntry : this.children.entrySet() ) copy.children.put( childEntry.getKey(), childEntry.getValue() );
		return copy;
	}
	
	@Override
	public String toString() {
		if ( this.children.size() == 0 ) return "{}";
		String str = "{";
		Iterator<Entry<String, OSFNode>> childEntriesIt = this.children.entrySet().iterator();
		while ( childEntriesIt.hasNext() ) {
			Entry<String, OSFNode> childEntry = childEntriesIt.next();
			str += childEntry.getKey() + ':' + childEntry.getValue().toString();
			if ( childEntriesIt.hasNext() ) str += ",";
		}
		return str + '}';
	}

}
