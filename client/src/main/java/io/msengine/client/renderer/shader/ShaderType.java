package io.msengine.client.renderer.shader;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;

/**
 *
 * @author Théo Rozier (Mindstorm38)
 *
 */
@Deprecated
public enum ShaderType {

	VERTEX   ("vertex",  ".vsh", GL_VERTEX_SHADER),
	FRAGMENT ("fragment",".fsh", GL_FRAGMENT_SHADER);
	
	public final String identifier;
	public final String extension;
	public final int index;
	protected final Map<String, ShaderLoader> shaders;
	
	private ShaderType(String identifier, String extension, int index) {
		
		this.identifier = identifier;
		this.extension = extension;
		this.index = index;
		this.shaders = new HashMap<>();
		
	}
	
}
