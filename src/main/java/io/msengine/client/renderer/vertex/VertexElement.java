package io.msengine.client.renderer.vertex;

import io.msengine.client.renderer.util.DataType;

/**
 *
 * A vertex element definition.
 *
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class VertexElement {
	
	// Constants \\
	
	public static final VertexElement POSITION_2F		= new VertexElement("position",       DataType.FLOAT, 2);
	public static final VertexElement POSITION_3f       = new VertexElement("position",       DataType.FLOAT, 3);
	public static final VertexElement COLOR_4F			= new VertexElement("color",          DataType.FLOAT, 4);
	public static final VertexElement TEX_COORD_2F		= new VertexElement("tex_coord",      DataType.FLOAT, 2);
	public static final VertexElement OCCL_TEX_COORD_2F	= new VertexElement("occl_tex_coord", DataType.FLOAT, 2);
	
	// Class \\
	
	protected final String identifier;
	protected final DataType type;
	protected final int count;
	
	protected final int size;
	
	public VertexElement(String identifier, DataType type, int count) {
		
		this.identifier = identifier;
		this.type = type;
		this.count = count;
		
		this.size = this.type.size * this.count;
		
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
	public String toString() {
		return this.identifier.toUpperCase() + "_" + this.count + this.type.suffix;
	}
	
}
