package io.msengine.client.graphics.gui.render;

import io.msengine.client.graphics.buffer.BufferArray;
import io.msengine.client.graphics.shader.uniform.SamplerUniform;
import io.msengine.client.graphics.util.DataType;

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
		
		setAttribDefault(this.attribColor, 1, 1, 1, 1);
		setAttribDefault(this.attribTexCoord, 0, 0);
		
	}
	
	public void setTextureUnit(int unit) {
		this.textureSampler.setTextureUnit(unit);
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
