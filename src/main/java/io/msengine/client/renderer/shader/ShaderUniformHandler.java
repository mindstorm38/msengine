package io.msengine.client.renderer.shader;

public interface ShaderUniformHandler {

	public static final ShaderDefaultUniform DEFAULT_UNIFORM = new ShaderDefaultUniform();
	
	public ShaderUniformBase getShaderUniformOrDefault(String identifier);
	public ShaderUniformBase getShaderUniform(String identifier);
	
}
