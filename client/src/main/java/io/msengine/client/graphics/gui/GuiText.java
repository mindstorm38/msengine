package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.font.Font;
import io.msengine.client.graphics.font.FontFamily;
import io.msengine.client.graphics.font.glyph.Glyph;
import io.msengine.client.graphics.font.glyph.GlyphPage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
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
	
	@Override
	protected void init() {
		if (this.isTextReady()) {
			this.updateTextBuffers();
		}
	}
	
	@Override
	protected void stop() {
		this.buffers.values().forEach(GuiBufferArray::close);
		this.buffers.clear();
	}
	
	@Override
	protected void render(float alpha) {
		
		if (this.isTextReady()) {
			
			if (this.updateBuffers) {
				this.updateTextBuffers();
			}
			
			this.model.push().translate(this.xOffset, this.yOffset).apply();
			
			this.manager.setTextureUnit(0);
			Texture.setTextureUnit(0);
			
			this.buffers.forEach((textureName, buf) -> {
				Texture.bindTexture(GL11.GL_TEXTURE_2D, textureName);
				buf.draw();
			});
			
			Texture.unbindTexture(GL11.GL_TEXTURE_2D);
			this.manager.setTextureUnit(null);
			
			this.model.pop();
			
		}
		
	}
	
	@Override
	protected void update() { }
	
	public boolean isTextReady() {
		return this.font != null && this.text != null && this.font.isValid();
	}
	
	public void setFont(Font font) {
		if (!font.isValid()) {
			throw new IllegalArgumentException("The given font is no longer valid.");
		} else if (this.font != font) {
			this.font = font;
			this.updateBuffers = true;
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
		return this.getProgram().createBuffer(false, true);
	}
	
	private void updateTextBuffers() {
		
		int[] codePoints = this.text.codePoints().toArray();
		int codePointsCount = codePoints.length;
		
		float[] codePointsOffsets = new float[codePointsCount];
		GlyphPage[] codePointsPages = new GlyphPage[codePointsCount];
		
		Map<Integer, TempBufferData> buffersCodePoints = new HashMap<>();
		
		GlyphPage page;
		for (int i = 0, codePoint; i < codePointsCount; ++i) {
			codePoint = codePoints[i];
			page = this.font.getGlyphPage(codePoint);
			codePointsPages[i] = page;
			buffersCodePoints.computeIfAbsent(page.getTextureName(), tex -> new TempBufferData()).codePointsCount++;
		}
		
		// System.out.println("codePoints=" + Arrays.toString(codePoints));
		// System.out.println("codePointsPages=" + Arrays.toString(codePointsPages));
		// System.out.println("buffersCodePoints=" + buffersCodePoints);
		
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
				
				codePoint = codePoints[i];
				page = codePointsPages[i];
				bufferData = buffersCodePoints.get(page.getTextureName());
				glyph = page.getGlyph(codePoint);
				
				// System.out.println("Draw '" + (char) codePoint + "' to " + x + "/" + y);
				
				glyph.putToBuffer(x, y, bufferData.dataBuffer);
				GuiCommon.putSquareIndices(bufferData.currentIndex, bufferData.indicesBuffer);
				
				bufferData.currentIndex += 4;
				x += (i + 1 < codePointsCount) ? glyph.getKernAdvance(codePoints[i + 1]) : glyph.getAdvance();
				
			}
			
			buffersCodePoints.forEach((textureName, temp) -> {
				
				temp.dataBuffer.flip();
				temp.indicesBuffer.flip();
				
				GuiBufferArray buf = this.buffers.get(textureName);
				buf.bindVao();
				buf.uploadVboData(0, temp.dataBuffer, BufferUsage.DYNAMIC_DRAW);
				buf.uploadIboData(temp.indicesBuffer, BufferUsage.DYNAMIC_DRAW);
				
			});
			
			this.codePoints = codePoints;
			this.codePointsOffsets = codePointsOffsets;
			
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
