package io.msengine.client.graphics.gui.render;

import io.msengine.client.graphics.buffer.BufferArray;
import io.msengine.client.graphics.shader.uniform.SamplerUniform;
import io.msengine.client.graphics.util.DataType;
import io.msengine.common.util.Color;

public class GuiProgramText extends GuiStdProgramBase {
	
	public static final GuiProgramType<GuiProgramText> TYPE = new GuiProgramType<>("text", GuiProgramText::new);
	
	private SamplerUniform textureSampler;
	private int attribColor;
	private int attribTexCoord;
	
	public GuiProgramText() {
		super(COMMON_VERTEX_SHADER, TEXT_FRAGMENT_SHADER);
	}
	
	@Override
	protected void postLink() {
		
		super.postLink();
		
		this.textureSampler = this.createSampler("texture_sampler");
		this.attribColor = this.getAttribLocation("color");
		this.attribTexCoord = this.getAttribLocation("tex_coord");
		
	}
	
	@Override
	public void use() {
		super.use();
		this.resetColorAttrib();
		this.setTexCoordAttrib(0, 0);
	}
	
	public void resetColorAttrib() {
		setAttribDefault(this.attribColor, 1, 1, 1, 1);
	}
	
	public void setColorAttrib(Color color) {
		setAttribDefault(this.attribColor, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public void setTexCoordAttrib(float u, float v) {
		setAttribDefault(this.attribTexCoord, u, v);
	}
	
	// Texture //
	
	public void setTextureUnit(int unit) {
		this.textureSampler.setTextureUnit(unit);
		this.textureSampler.upload();
	}
	
	// Buffer //
	
	public GuiBufferArray createBuffer(boolean color) {
		return BufferArray.newBuilder(GuiBufferArray::new)
				.newBuffer()
					.withAttrib(this.attribPosition, DataType.FLOAT, 2)
					.withAttrib(this.attribTexCoord, DataType.FLOAT, 2)
					.withCond(color, b -> b.withAttrib(this.attribColor, DataType.FLOAT, 4))
					.build()
				.withVertexAttrib(this.attribPosition, true)
				.withVertexAttrib(this.attribTexCoord, true)
				.withVertexAttrib(this.attribColor, color)
				.build();
	}
	
}
