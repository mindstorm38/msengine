package io.msengine.client.renderer.vertex;

import io.sutil.CollectionUtils;

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
	
	public VertexBufferFormat getBuffer(int index) {
		return this.buffers[ index ];
	}
	
	public VertexBufferFormat getBuffer(String identifier) {
		for ( VertexBufferFormat buffer : this.buffers )
			if ( buffer.identifier.equals( identifier ) )
				return buffer;
		return null;
	}
	
	public int getBufferIndex(String identifier) {
		for ( int i = 0; i < this.buffers.length; i++ )
			if ( this.buffers[ i ].identifier.equals( identifier ) )
				return i;
		return -1;
	}
	
	public VertexBufferFormat getBufferForElement(VertexElement element) {
		for ( VertexBufferFormat buffer : this.buffers )
			if ( buffer.hasElement( element ) )
				return buffer;
		return null;
	}
	
	public int getBufferIndexForElement(VertexElement element) {
		for ( int i = 0; i < this.buffers.length; i++ )
			if ( this.buffers[ i ].hasElement( element ) )
				return i;
		return -1;
	}
	
	public int getBufferIndex(VertexBufferFormat buffer) {
		return CollectionUtils.arrayIndexOf( this.buffers, buffer );
	}
	
	private void validate() {
		
		for ( VertexBufferFormat buffer : this.buffers ) {
			
			for ( VertexElement element : buffer.elements ) {
				
				for ( VertexBufferFormat _buffer : this.buffers ) {
					
					if ( _buffer == buffer ) continue;
					
					if ( _buffer.hasElement( element ) )
						throw new IllegalBufferFormatException( "Element " + element.toString() + " present in multiple buffers" );
					
				}
				
			}
			
		}
		
	}
	
}
