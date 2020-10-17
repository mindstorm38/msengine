package io.msengine.client.graphics.gui.render;

import io.msengine.client.EngineClient;
import io.msengine.client.graphics.buffer.BufferArray;
import io.msengine.client.graphics.shader.Shader;
import io.msengine.client.graphics.shader.ShaderProgram;
import io.msengine.client.graphics.shader.ShaderType;
import io.msengine.client.graphics.shader.uniform.Float4Uniform;
import io.msengine.client.graphics.shader.uniform.FloatMatrix4Uniform;
import io.msengine.client.graphics.shader.uniform.Int1Uniform;
import io.msengine.client.graphics.shader.uniform.SamplerUniform;
import io.msengine.common.util.Color;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiShaderProgram extends ShaderProgram {

	private FloatMatrix4Uniform projectionMatrixUniform;
	private FloatMatrix4Uniform modelMatrixUniform;
	private Float4Uniform globalColorUniform;
	private Int1Uniform textureEnabledUniform;
	private SamplerUniform textureSampler;
	
	protected Shader createVertexShader() throws IOException {
		return Shader.fromSource(ShaderType.VERTEX, EngineClient.ASSETS.openAssetStreamExcept("mse/shaders/gui.vsh"));
	}
	
	protected Shader createFragmentShader() throws IOException {
		return Shader.fromSource(ShaderType.FRAGMENT, EngineClient.ASSETS.openAssetStreamExcept("mse/shaders/gui.fsh"));
	}
	
	@Override
	protected void preLink() {
		
		super.preLink();
		
		Shader vsh, fsh;
		
		try {
			vsh = this.createVertexShader();
			fsh = this.createFragmentShader();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create GUI shaders.", e);
		}
		
		vsh.compile();
		fsh.compile();
		
		this.attachShader(vsh);
		this.attachShader(fsh);
		
	}
	
	@Override
	protected void postLink() {
		
		super.postLink();
		
		this.projectionMatrixUniform = this.createUniform("projection_matrix", FloatMatrix4Uniform::new);
		this.modelMatrixUniform = this.createUniform("model_matrix", FloatMatrix4Uniform::new);
		this.globalColorUniform = this.createUniform("global_color", Float4Uniform::new);
		this.textureEnabledUniform = this.createUniform("texture_enabled", Int1Uniform::new);
		this.textureSampler = this.createSampler("texture_sampler");
		
		this.deleteAttachedShaders();
		
	}
	
	public void setProjectionMatrix(Matrix4f mat) {
		this.projectionMatrixUniform.set(mat);
	}
	
	public void setModelMatrix(Matrix4f mat) {
		this.modelMatrixUniform.set(mat);
	}
	
	public void setGlobalColor(Color color) {
		this.globalColorUniform.set(color);
	}
	
	public void setTextureEnabled(boolean enabled) {
		this.textureEnabledUniform.set(enabled);
	}
	
	public void setActiveTexture(Integer activeTexture) {
		this.setTextureEnabled(activeTexture != null);
		if (activeTexture != null) {
			this.textureSampler.setActiveTexture(activeTexture);
		}
	}
	
	public GuiBufferArray createBuffer(boolean color, boolean tex) {
		return BufferArray.newBuilder(GuiBufferArray::new)
				.newBuffer()
					.withAttrib(this.getAttribLocation("position"), GL11.GL_FLOAT, 2)
					.withCond(color, bb -> bb.withAttrib(this.getAttribLocation("color"), GL11.GL_FLOAT, 4))
					.withCond(color, bb -> bb.withAttrib(this.getAttribLocation("tex_coord"), GL11.GL_FLOAT, 2))
					.build()
				.build();
	}
	
}
