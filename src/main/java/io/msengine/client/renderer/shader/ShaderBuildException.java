package io.msengine.client.renderer.shader;

public class ShaderBuildException extends RuntimeException {

	private static final long serialVersionUID = -1255384970660732014L;

	public ShaderBuildException(String message, Throwable t) {
		super( message, t );
	}
	
	public ShaderBuildException(Throwable t) {
		super( t );
	}
	
	public ShaderBuildException(String message) {
		super( message );
	}
	
	public ShaderBuildException() {
		super();
	}
	
}
