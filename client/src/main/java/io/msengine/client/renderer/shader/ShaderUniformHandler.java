package io.msengine.client.renderer.shader;

/**
 *
 * Common interface for all handlers of GL uniforms.
 *
 * @author Théo Rozier (Mindstorm38)
 *
 */
public interface ShaderUniformHandler {

	ShaderDefaultUniform DEFAULT_UNIFORM = new ShaderDefaultUniform();
	
	/**
	 * Get uniform from its identifier, or return the default uniform ({@link ShaderUniformHandler#DEFAULT_UNIFORM}).
	 * @param identifier The shader uniform identifier.
	 * @return The uniform or default uniform if not existing in the handler.
	 */
	ShaderUniformBase getShaderUniformOrDefault(String identifier);
	
	/**
	 * Get uniform from its identifier.
	 * @param identifier The shader uniform identifier.
	 * @return The uniform or null if not existing in the handler.
	 */
	ShaderUniformBase getShaderUniform(String identifier);
	
}
