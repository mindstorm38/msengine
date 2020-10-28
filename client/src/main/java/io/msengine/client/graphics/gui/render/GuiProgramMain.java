package io.msengine.client.graphics.gui.render;

import io.msengine.client.graphics.buffer.BufferArray;
import io.msengine.client.graphics.texture.base.Texture;
import io.msengine.client.graphics.util.DataType;
import io.msengine.client.graphics.shader.uniform.Float4Uniform;
import io.msengine.client.graphics.shader.uniform.Int1Uniform;
import io.msengine.client.graphics.shader.uniform.SamplerUniform;
import io.msengine.common.util.Color;
import org.lwjgl.opengl.GL11;

public class GuiProgramMain extends GuiStdProgramBase {

	public static final GuiProgramType<GuiProgramMain> TYPE = new GuiProgramType<>("main", GuiProgramMain::new);
	
	private Float4Uniform globalColorUniform;
	private Int1Uniform textureEnabledUniform;
	private SamplerUniform textureSampler;
	
	private int attribColor;
	private int attribTexCoord;
	
	public GuiProgramMain() {
		super(COMMON_VERTEX_SHADER, MAIN_FRAGMENT_SHADER);
	}
	
	@Override
	protected void postLink() {
		
		super.postLink();
		
		this.globalColorUniform = this.createUniform("global_color", Float4Uniform::new);
		this.textureEnabledUniform = this.createUniform("texture_enabled", Int1Uniform::new);
		this.textureSampler = this.createSampler("texture_sampler");
		
		this.attribColor = this.getAttribLocation("color");
		this.attribTexCoord = this.getAttribLocation("tex_coord");
		
		setAttribDefault(this.attribColor, 1, 1, 1, 1);
		setAttribDefault(this.attribTexCoord, 0, 0);
		
	}
	
	public void setGlobalColor(Color color) {
		this.globalColorUniform.set(color);
	}
	
	// Texture //
	
	public void setTextureUnit(Integer unitOrNull) {
		this.textureEnabledUniform.set(unitOrNull != null);
		if (unitOrNull != null) {
			this.textureSampler.setTextureUnit(unitOrNull);
		}
	}
	
	public void setTextureUnitAndBind(int unit, int name) {
		this.setTextureUnit(unit);
		Texture.bindTexture(unit, GL11.GL_TEXTURE_2D, name);
	}
	
	public void resetTextureUnitAndUnbind() {
		this.setTextureUnit(null);
		Texture.unbindTexture(GL11.GL_TEXTURE_2D);
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
