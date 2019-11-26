package io.msengine.client.renderer.basic;

import io.msengine.client.renderer.framebuffer.Framebuffer;
import io.msengine.client.renderer.shader.ShaderManager;
import io.msengine.client.renderer.shader.ShaderSamplerObject;
import io.msengine.client.renderer.shader.ShaderUniformBase;
import io.msengine.client.renderer.shader.ShaderValueType;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.client.renderer.vertex.VertexElement;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL20.*;

public abstract class BasicShaderManager extends ShaderManager {
	
	public static final String BASIC_GLOBAL_MATRIX   = "global_matrix";
	public static final String BASIC_TEXTURE_SAMPLER = "texture_sampler";
	public static final String BASIC_TEXTURE_ENABLED = "texture_enabled";
	
	public BasicShaderManager(String identifier, String vertexShaderIdentifier, String fragmentShaderIdentifier, VertexElement position) {
		
		super(identifier, vertexShaderIdentifier, fragmentShaderIdentifier);
		
		this.registerAttribute(position);
		this.registerAttribute(VertexElement.COLOR_4F);
		this.registerAttribute(VertexElement.TEX_COORD_2F);
		
		this.registerUniform(BASIC_GLOBAL_MATRIX, ShaderValueType.MAT4);
		this.registerUniform(BASIC_TEXTURE_ENABLED, ShaderValueType.INT);
		this.registerSampler(BASIC_TEXTURE_SAMPLER);
		
	}
	
	public BasicShaderManager(String identifier, String vertexShaderIdentifier, VertexElement position) {
		this(identifier, vertexShaderIdentifier, "basic", position);
	}
	
	@Override
	public void build() {
		
		super.build();
		
		this.getShaderAttributeLocationSafe(VertexElement.COLOR_4F, loc -> glVertexAttrib4f(loc, 1f, 1f, 1f, 1f));
		this.getShaderAttributeLocationSafe(VertexElement.TEX_COORD_2F, loc -> glVertexAttrib2f(loc, 0f, 0f));
		
		this.setTextureSampler(null);
		
	}
	
	public void setTextureSampler(ShaderSamplerObject sampler) {
		
		this.setSamplerObject(BASIC_TEXTURE_SAMPLER, sampler);
		this.getShaderUniformOrDefault(BASIC_TEXTURE_ENABLED).set(sampler != null);
		
	}
	
	public ShaderUniformBase getGlobalMatrixUniform() {
		return this.getShaderUniformOrDefault(BASIC_GLOBAL_MATRIX);
	}
	
	public void setGlobalMatrix(Matrix4f mat) {
		this.getGlobalMatrixUniform().set(mat);
	}
	
	public abstract IndicesDrawBuffer createBasicDrawBuffer(boolean color, boolean texcoords);
	
}
