package io.msengine.client.renderer.vertex;

import io.sutil.CollectionUtils;

public class VertexBufferFormat {
	
	// Class \\
	
	protected final String identifier;
	
	protected final VertexElement[] elements;
	protected final int[] offsets;
	protected final int size;
	
	public VertexBufferFormat(VertexElement element) {
		
		this.identifier = element.identifier;
		
		this.elements = new VertexElement[] { element };
		this.offsets = new int[] { 0 };
		this.size = element.size;
		
	}
	
	public VertexBufferFormat(String identifier, VertexElement...elements) {
		
		this.identifier = identifier;
		
		this.elements = new VertexElement[ elements.length ];
		this.offsets = new int[ elements.length ];
		
		VertexElement element;
		int nextOffset = 0;
		
		for ( int i = 0; i < elements.length; i++ ) {
			
			element = this.elements[ i ] = elements[ i ];
			this.offsets[ i ] = nextOffset;
			
			nextOffset += element.size;
			
		}
		
		this.size = nextOffset;
		
		this.validate();
		
	}
	
	public boolean hasElement(VertexElement element) {
		return CollectionUtils.arrayContains( this.elements, element );
	}
	
	public int getElementCount() {
		return this.elements.length;
	}
	
	public VertexElement getElement(int idx) {
		return this.elements[ idx ];
	}
	
	public int getElementOffset(VertexElement element) {
		int index = CollectionUtils.arrayIndexOf( this.elements, element );
		return index == -1 ? -1 : this.offsets[ index ];
	}
	
	private void validate() {
		
		for ( VertexElement element : this.elements )
			if ( CollectionUtils.arrayIndexOf( this.elements, element ) != CollectionUtils.arrayLastIndexOf( this.elements, element ) )
				throw new IllegalBufferFormatException( "Element " + element + " present multiple times in the buffer format" );
		
	}
	
}
