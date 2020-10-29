package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.font.Font;
import io.msengine.client.graphics.font.FontFamily;
import io.msengine.client.graphics.font.glyph.Glyph;
import io.msengine.client.graphics.font.glyph.GlyphPage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.graphics.gui.render.GuiProgramText;
import io.msengine.client.graphics.texture.base.Texture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class GuiText extends GuiObject {
	
	protected final Map<Integer, GuiBufferArray> buffers = new HashMap<>();
	protected boolean updateBuffers;
	
	protected Font font;
	protected String text;
	protected int[] codePoints;
	protected float[] codePointsOffsets;
	protected float textWidth;
	
	public GuiText(FontFamily family, float size, String text) {
		this.setFont(family, size);
		this.setText(text);
	}
	
	public GuiText(Font font, String text) {
		this.setFont(font);
		this.setText(text);
	}
	
	public GuiText(FontFamily family, float size) {
		this.setFont(family, size);
	}
	
	public GuiText(Font font) {
		this.setFont(font);
	}
	
	public GuiText(String text) {
		this.setText(text);
	}
	
	@Override
	protected void init() {
		this.acquireProgram(GuiProgramText.TYPE);
		if (this.isTextReady()) {
			this.updateTextBuffers();
		}
	}
	
	@Override
	protected void stop() {
		this.releaseProgram(GuiProgramText.TYPE);
		this.buffers.values().forEach(GuiBufferArray::close);
		this.buffers.clear();
	}
	
	@Override
	protected void render(float alpha) {
		
		if (this.isTextReady()) {
			
			if (this.updateBuffers) {
				this.updateTextBuffers();
			}
			
			GuiProgramText program = this.useProgram(GuiProgramText.TYPE);
			
			this.model.push().translate(this.xOffset, this.yOffset).apply();
			
			program.setTextureUnit(0);
			Texture.setTextureUnit(0);
			
			this.buffers.forEach((textureName, buf) -> {
				Texture.bindTexture(GL11.GL_TEXTURE_2D, textureName);
				buf.draw();
			});
			
			Texture.unbindTexture(GL11.GL_TEXTURE_2D);
			
			this.model.pop();
			
		}
		
	}
	
	@Override
	protected void update() { }
	
	@Override
	public float getAutoWidth() {
		return this.textWidth;
	}
	
	@Override
	public float getAutoHeight() {
		return this.font == null ? 0 : this.font.getSize();
	}
	
	public boolean isTextReady() {
		return this.font != null && this.text != null && this.font.isValid();
	}
	
	public void setFont(Font font) {
		if (!font.isValid()) {
			throw new IllegalArgumentException("The given font is no longer valid.");
		} else if (this.font != font) {
			this.font = font;
			this.updateBuffers = true;
			// Update Y offset because the new font size can be used with auto height.
			this.updateYOffset();
		}
	}
	
	public void setFont(FontFamily family, float size) {
		this.setFont(family.getSize(size));
	}
	
	public void setText(String text) {
		if (!text.equals(this.text)) {
			this.text = text;
			this.updateBuffers = true;
		}
	}
	
	private GuiBufferArray createTextBufferArray() {
		return this.getProgram(GuiProgramText.TYPE).createBuffer(false);
	}
	
	private void updateTextBuffers() {
		
		this.codePoints = this.text.codePoints().toArray();
		int codePointsCount = this.codePoints.length;
		
		this.codePointsOffsets = new float[codePointsCount];
		
		GlyphPage[] codePointsPages = new GlyphPage[codePointsCount];
		Map<Integer, TempBufferData> buffersCodePoints = new HashMap<>();
		
		GlyphPage page;
		for (int i = 0, codePoint; i < codePointsCount; ++i) {
			codePoint = this.codePoints[i];
			page = this.font.getGlyphPage(codePoint);
			codePointsPages[i] = page;
			buffersCodePoints.computeIfAbsent(page.getTextureName(), tex -> new TempBufferData()).codePointsCount++;
		}
		
		this.buffers.entrySet().removeIf(e -> {
			if (!buffersCodePoints.containsKey(e.getKey())) {
				e.getValue().close();
				return true;
			} else {
				return false;
			}
		});
		
		try {
			
			buffersCodePoints.forEach((textureName, temp) -> {
				
				GuiBufferArray buf = this.buffers.get(textureName);
				if (buf == null) {
					buf = this.createTextBufferArray();
					this.buffers.put(textureName, buf);
				}
				
				temp.dataBuffer = MemoryUtil.memAllocFloat(temp.codePointsCount * 8 * 2);
				temp.indicesBuffer = MemoryUtil.memAllocInt(buf.setIndicesCount(temp.codePointsCount * 6));
				
			});
			
			TempBufferData bufferData;
			Glyph glyph;
			
			float x = 0;
			float y = 0;
			
			for (int i = 0, codePoint; i < codePointsCount; ++i) {
				
				codePoint = this.codePoints[i];
				page = codePointsPages[i];
				bufferData = buffersCodePoints.get(page.getTextureName());
				glyph = page.getGlyph(codePoint);
				
				glyph.putToBuffer(x, y, bufferData.dataBuffer);
				GuiCommon.putSquareIndices(bufferData.currentIndex, bufferData.indicesBuffer);
				
				bufferData.currentIndex += 4;
				x += (i + 1 < codePointsCount) ? glyph.getKernAdvance(this.codePoints[i + 1]) : glyph.getAdvance();
				
				this.codePointsOffsets[i] = x;
				
			}
			
			this.textWidth = x;
			
			buffersCodePoints.forEach((textureName, temp) -> {
				
				temp.dataBuffer.flip();
				temp.indicesBuffer.flip();
				
				GuiBufferArray buf = this.buffers.get(textureName);
				buf.bindVao();
				buf.uploadVboData(0, temp.dataBuffer, BufferUsage.DYNAMIC_DRAW);
				buf.uploadIboData(temp.indicesBuffer, BufferUsage.DYNAMIC_DRAW);
				
			});
			
			// Update X offset because textSize is updated and can be used through auto width.
			this.updateXOffset();
			
		} finally {
			
			buffersCodePoints.values().forEach(temp -> {
				if (temp.dataBuffer != null) MemoryUtil.memFree(temp.dataBuffer);
				if (temp.indicesBuffer != null) MemoryUtil.memFree(temp.indicesBuffer);
			});
			
		}
		
		this.updateBuffers = false;
		
	}
	
	private static class TempBufferData {
		
		private int codePointsCount;
		private FloatBuffer dataBuffer;
		private IntBuffer indicesBuffer;
		private int currentIndex;
		
		@Override
		public String toString() {
			return "TempBufferData<" +
					"codePointsCount=" + codePointsCount +
					", dataBuffer=" + dataBuffer +
					", indicesBuffer=" + indicesBuffer +
					'>';
		}
		
	}
	
}
