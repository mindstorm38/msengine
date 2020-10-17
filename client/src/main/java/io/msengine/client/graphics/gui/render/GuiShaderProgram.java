package io.msengine.client.graphics.gui.render;

import io.msengine.client.EngineClient;
import io.msengine.client.graphics.shader.Shader;
import io.msengine.client.graphics.shader.ShaderProgram;
import io.msengine.client.graphics.shader.ShaderType;
import io.msengine.client.graphics.shader.uniform.Float4Uniform;
import io.msengine.client.graphics.shader.uniform.FloatMatrix4Uniform;
import io.msengine.client.graphics.shader.uniform.Int1Uniform;
import io.msengine.client.graphics.shader.uniform.SamplerUniform;
import io.msengine.common.util.Color;
import org.joml.Matrix4f;

import java.io.IOException;

public class GuiShaderProgram extends ShaderProgram {

	private FloatMatrix4Uniform globalMatrixUniform;
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
		
		this.globalMatrixUniform = this.createUniform("global_matrix", FloatMatrix4Uniform::new);
		this.globalColorUniform = this.createUniform("global_color", Float4Uniform::new);
		this.textureEnabledUniform = this.createUniform("texture_enabled", Int1Uniform::new);
		this.textureSampler = this.createSampler("texture_sampler");
		
		this.deleteAttachedShaders();
		
	}
	
	public void setGlobalMatrix(Matrix4f mat) {
		this.globalMatrixUniform.set(mat);
	}
	
	public void setGlobalColor(Color color) {
		this.globalColorUniform.set(color);
	}
	
	public void setActiveTexture(Integer activeTexture) {
		this.textureEnabledUniform.set(activeTexture != null);
		if (activeTexture != null) {
			this.textureSampler.setActiveTexture(activeTexture);
		}
	}
	
}
