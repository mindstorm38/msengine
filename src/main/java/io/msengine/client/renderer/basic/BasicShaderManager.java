package io.msengine.client.renderer.basic;

import io.msengine.client.renderer.shader.ShaderManager;
import io.msengine.client.renderer.shader.ShaderValueType;
import io.msengine.client.renderer.vertex.VertexElement;

import static io.msengine.client.renderer.vertex.type.BasicFormat.*;

public abstract class BasicShaderManager extends ShaderManager {
	
	public static final String BASIC_GLOBAL_MATRIX   = "global_matrix";
	public static final String BASIC_TEXTURE_SAMPLER = "texture_sampler";
	
	public BasicShaderManager(String identifier, String vertexShaderIdentifier, VertexElement position) {
		
		super(identifier, vertexShaderIdentifier, "basic");
		
		this.registerAttribute(VertexElement.COLOR_4F);
		this.registerAttribute(VertexElement.TEX_COORD_2F);
		
		this.registerUniform(BASIC_GLOBAL_MATRIX, ShaderValueType.MAT4);
		this.registerSampler(BASIC_TEXTURE_SAMPLER);
		
	}
	
}
