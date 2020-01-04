package io.msengine.client.renderer.vertex;

import io.sutil.CollectionUtils;

/**
 *
 * VAO Format.
 *
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class VertexArrayFormat {
	
	// Class \\
	
	protected final VertexBufferFormat[] buffers;
	
	public VertexArrayFormat(VertexBufferFormat...buffers) {
		
		this.buffers = buffers;
		
		this.validate();
		
	}
	
	public VertexArrayFormat(VertexElement...elements) {
		
		this.buffers = new VertexBufferFormat[ elements.length ];
		
		for ( int i = 0; i < elements.length; i++ )
			this.buffers[ i ] = new VertexBufferFormat( elements[ i ] );
		
		this.validate();
		
	}
	
	public int getBufferCount() {
		return this.buffers.length;
	}
	
	/**
	 * Get the VBO format at specified index in this VAO format.
	 * @param index The index.
	 * @return The VBO format found at this index.
	 */
	public VertexBufferFormat getBuffer(int index) {
		return this.buffers[ index ];
	}
	
	/**
	 * get the VBO format from its identifier.
	 * @param identifier The identifier or searched VBO format.
	 * @return The VBO format corresponding to this identifier, or Null if not found.
	 */
	public VertexBufferFormat getBuffer(String identifier) {
		
		for ( VertexBufferFormat buffer : this.buffers )
			if ( buffer.identifier.equals( identifier ) )
				return buffer;
			
		return null;
		
	}
	
	/**
	 * Get the VBO format index defined by this identifier.
	 * @param identifier The VBO format identifier to search for.
	 * @return The index of the VBO format, or -1 if not found.
	 */
	public int getBufferIndex(String identifier) {
		
		for ( int i = 0; i < this.buffers.length; i++ )
			if ( this.buffers[ i ].identifier.equals( identifier ) )
				return i;
			
		return -1;
		
	}
	
	/**
	 * Get the VBO format that contains the specified vertex element definition.
	 * @param element The vertex element definition.
	 * @return The VBO format that contains this element, or Null if not found.
	 */
	public VertexBufferFormat getBufferForElement(VertexElement element) {
		
		for ( VertexBufferFormat buffer : this.buffers )
			if ( buffer.hasElement( element ) )
				return buffer;
			
		return null;
		
	}
	
	/**
	 * Get the VBO format index in this VAO format that contains
	 * the specified vertex element definition.
	 * @param element The vertex element definition.
	 * @return The VBO format inde in this VAO format, or -1 if not found.
	 */
	public int getBufferIndexForElement(VertexElement element) {
		
		for ( int i = 0; i < this.buffers.length; i++ )
			if ( this.buffers[ i ].hasElement( element ) )
				return i;
			
		return -1;
		
	}
	
	/**
	 * Get the VBO format index in this VAO format.
	 * @param buffer The VBO format.
	 * @return The VBO format index.
	 */
	public int getBufferIndex(VertexBufferFormat buffer) {
		return CollectionUtils.arrayIndexOf( this.buffers, buffer );
	}
	
	/**
	 * Internal method to validate this VAO format.
	 * @throws IllegalBufferFormatException If a vertex element definition if used multiple time, in different buffers.
	 */
	private void validate() {
		
		for ( VertexBufferFormat buffer : this.buffers )
			for ( VertexElement element : buffer.elements )
				for ( VertexBufferFormat _buffer : this.buffers )
					if ( _buffer != buffer && _buffer.hasElement( element ) )
						throw new IllegalBufferFormatException( "Element " + element.toString() + " present in multiple buffers" );
		
	}
	
}
