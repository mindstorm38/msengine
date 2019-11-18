package io.msengine.client.renderer.vertex;

import io.sutil.CollectionUtils;

/**
 *
 * VBO format specify how attributes are stored in a Vertex Buffer Object.
 *
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class VertexBufferFormat {
	
	// Class \\
	
	protected final String identifier;
	
	protected final VertexElement[] elements;
	protected final int[] offsets;
	protected final int size;
	
	/**
	 * Construct a VBO format with a single vertex element definition.<br>
	 * The identifier of this will be set to the element identifier.
	 * @param element The only vertex element in the VBO format.
	 */
	public VertexBufferFormat(VertexElement element) {
		
		this.identifier = element.identifier;
		
		this.elements = new VertexElement[] { element };
		this.offsets = new int[] { 0 };
		this.size = element.size;
		
	}
	
	/**
	 * Construct a VBO format using a specific identifier and one or more vertex element definition.
	 * @param identifier The identifier for this format.
	 * @param elements One or more vertex element definitions.
	 */
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
	
	/**
	 * Check if an vertex element definition is contained in this format.
	 * @param element The element to check.
	 * @return True if the vertex element definition is contained in this format.
	 */
	public boolean hasElement(VertexElement element) {
		return CollectionUtils.arrayContains( this.elements, element );
	}
	
	/**
	 * @return The element count in this format.
	 */
	public int getElementCount() {
		return this.elements.length;
	}
	
	/**
	 * Get a vertex element at a specified index.
	 * @param idx The vertex element index.
	 * @return Vertex element at the specified index.
	 */
	public VertexElement getElement(int idx) {
		return this.elements[ idx ];
	}
	
	/**
	 * Byte count offset of a specific vertex element definition in this VBO format.
	 * @param element The vertex element definition to search for.
	 * @return The byte offset of the element in the VBO, or -1 if not found in this format.
	 */
	public int getElementOffset(VertexElement element) {
		
		int index = CollectionUtils.arrayIndexOf( this.elements, element );
		return index == -1 ? -1 : this.offsets[ index ];
		
	}
	
	/**
	 * Internal method to validate a VBO format.
	 * @throws IllegalBufferFormatException If a vertex element definition if defined multiple time.
	 */
	private void validate() {
		
		for ( VertexElement element : this.elements )
			if ( CollectionUtils.arrayIndexOf( this.elements, element ) != CollectionUtils.arrayLastIndexOf( this.elements, element ) )
				throw new IllegalBufferFormatException( "Element " + element + " present multiple times in the buffer format" );
		
	}
	
}
