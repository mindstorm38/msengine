package io.msengine.client.renderer.vertex;

import io.msengine.client.renderer.util.DataType;

import java.util.Objects;

/**
 *
 * A vertex element definition.
 *
 * @author Théo Rozier (Mindstorm38)
 *
 */
@Deprecated
public class VertexElement {
	
	// Constants \\
	
	public static final VertexElement POSITION_2F		= new VertexElement("position",       DataType.FLOAT, 2);
	public static final VertexElement POSITION_3F       = new VertexElement("position",       DataType.FLOAT, 3);
	public static final VertexElement COLOR_3F          = new VertexElement("color",          DataType.FLOAT, 3);
	public static final VertexElement COLOR_4F			= new VertexElement("color",          DataType.FLOAT, 4);
	public static final VertexElement TEX_COORD_2F		= new VertexElement("tex_coord",      DataType.FLOAT, 2);
	public static final VertexElement OCCL_TEX_COORD_2F	= new VertexElement("occl_tex_coord", DataType.FLOAT, 2);
	
	// Class \\
	
	private final String identifier;
	private final DataType type;
	private final int count;
	
	private final int size;
	private final int hash;
	
	public VertexElement(String identifier, DataType type, int count) {
		
		this.identifier = Objects.requireNonNull(identifier);
		this.type = Objects.requireNonNull(type);
		this.count = count;
		
		this.size = this.type.size * this.count;
		this.hash = Objects.hash(identifier, type, count);
		
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public DataType getType() {
		return this.type;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public int getSize() {
		return this.size;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj == this ) return true;
		if ( obj == null ) return false;
		if ( !( obj instanceof VertexElement ) ) return false;
		VertexElement o = (VertexElement) obj;
		return o.identifier.equals( this.identifier ) && o.type == this.type && o.count == this.count;
	}
	
	@Override
	public int hashCode() {
		return this.hash;
	}
	
	@Override
	public String toString() {
		return this.identifier.toUpperCase() + "_" + this.count + this.type.suffix;
	}
	
}
