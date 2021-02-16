package io.msengine.client.renderer.vertex;

/**
 *
 * Exception thrown if a VAO or VBO format is invalid.
 *
 * @author Théo Rozier (Mindstorm38)
 *
 */
@Deprecated
public class IllegalBufferFormatException extends RuntimeException {

	private static final long serialVersionUID = 5496760765895377452L;

	public IllegalBufferFormatException() {
		super();
	}
	
	public IllegalBufferFormatException(String msg) {
		super( msg );
	}
	
}
