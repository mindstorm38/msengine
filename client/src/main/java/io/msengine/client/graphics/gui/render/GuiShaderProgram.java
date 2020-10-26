package io.msengine.client.graphics.gui.render;

import io.msengine.client.EngineClient;
import io.msengine.client.graphics.buffer.BufferArray;
import io.msengine.client.graphics.shader.Shader;
import io.msengine.client.graphics.shader.ShaderProgram;
import io.msengine.client.graphics.util.DataType;
import io.msengine.client.graphics.shader.ShaderType;
import io.msengine.client.graphics.shader.uniform.Float4Uniform;
import io.msengine.client.graphics.shader.uniform.FloatMatrix4Uniform;
import io.msengine.client.graphics.shader.uniform.Int1Uniform;
import io.msengine.client.graphics.shader.uniform.SamplerUniform;
import io.msengine.common.util.Color;
import org.joml.Matrix4f;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class GuiShaderProgram extends ShaderProgram {

	private FloatMatrix4Uniform projectionMatrixUniform;
	private FloatMatrix4Uniform modelMatrixUniform;
	private Float4Uniform globalColorUniform;
	private Int1Uniform textureEnabledUniform;
	private SamplerUniform textureSampler;
	
	private int attribPosition;
	private int attribColor;
	private int attribTexCoord;
	
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
		this.deleteAttachedShaders();
		
		this.projectionMatrixUniform = this.createUniform("projection_matrix", FloatMatrix4Uniform::new);
		this.modelMatrixUniform = this.createUniform("model_matrix", FloatMatrix4Uniform::new);
		this.globalColorUniform = this.createUniform("global_color", Float4Uniform::new);
		this.textureEnabledUniform = this.createUniform("texture_enabled", Int1Uniform::new);
		this.textureSampler = this.createSampler("texture_sampler");
		
		this.attribPosition = this.getAttribLocation("position");
		this.attribColor = this.getAttribLocation("color");
		this.attribTexCoord = this.getAttribLocation("tex_coord");
		
		glVertexAttrib4f(this.attribColor, 1, 1, 1, 1);
		glVertexAttrib2f(this.attribTexCoord, 0, 0);
		
	}
	
	public void setProjectionMatrix(Matrix4f mat) {
		this.projectionMatrixUniform.set(mat);
	}
	
	public void uploadProjectionMatrix() {
		this.projectionMatrixUniform.uploadIfChanged();
	}
	
	public void setModelMatrix(Matrix4f mat) {
		this.modelMatrixUniform.set(mat);
	}
	
	public void setGlobalColor(Color color) {
		this.globalColorUniform.set(color);
	}
	
	// Texture //
	
	public void setTextureEnabled(boolean enabled) {
		this.textureEnabledUniform.set(enabled);
	}
	
	public void setTextureUnit(int unit) {
		this.textureSampler.setTextureUnit(unit);
	}
	
	public void setTextureUnit(Integer unitOrNull) {
		this.textureEnabledUniform.set(unitOrNull != null);
		if (unitOrNull != null) {
			this.textureSampler.setTextureUnit(unitOrNull);
		}
	}
	
	// Buffer //
	
	public GuiBufferArray createBuffer(boolean color, boolean tex) {
		return BufferArray.newBuilder(GuiBufferArray::new)
				.newBuffer()
					.withAttrib(this.attribPosition, DataType.FLOAT, 2)
					.withCond(color, b -> b.withAttrib(this.attribColor, DataType.FLOAT, 4))
					.withCond(tex, b -> b.withAttrib(this.attribTexCoord, DataType.FLOAT, 2))
					.build()
				.withVertexAttrib(this.attribPosition, true)
				.withVertexAttrib(this.attribColor, color)
				.withVertexAttrib(this.attribTexCoord, tex)
				.build();
	}
	
	public GuiBufferArray createBufferSep(boolean color, boolean tex) {
		return BufferArray.newBuilder((vao, vbos) -> new GuiBufferArray(vao, vbos, color ? 1 : 0, tex ? (color ? 2 : 1) : 0))
				.newBuffer().withAttrib(this.attribPosition, DataType.FLOAT, 2).build()
				.withCond(color, b -> b.newBuffer().withAttrib(this.attribColor, DataType.FLOAT, 4).build())
				.withCond(tex, b -> b.newBuffer().withAttrib(this.attribTexCoord, DataType.FLOAT, 2).build())
				.withVertexAttrib(this.attribPosition, true)
				.withVertexAttrib(this.attribColor, color)
				.withVertexAttrib(this.attribTexCoord, tex)
				.build();
	}
	
}
