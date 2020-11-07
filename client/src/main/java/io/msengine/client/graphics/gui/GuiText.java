package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.font.Font;
import io.msengine.client.graphics.font.FontFamily;
import io.msengine.client.graphics.font.glyph.Glyph;
import io.msengine.client.graphics.font.glyph.GlyphPage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.graphics.gui.render.GuiProgramText;
import io.msengine.client.graphics.texture.base.Texture;
import io.msengine.common.util.Color;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GuiText extends GuiObject {
	
	protected final Map<Integer, GuiBufferArray> buffers = new HashMap<>();
	protected boolean updateBuffers;
	
	protected Font font;
	protected String text;
	protected int[] codePoints;
	protected float[] codePointsOffsets;
	protected float textWidth;
	protected boolean ignoreDescent;
	protected Map<Integer, Effect> effects;
	
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
	
	public GuiText() { }
	
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
		if (this.font == null) {
			return 0;
		} else {
			float height = this.font.getSize();
			if (this.ignoreDescent) {
				height += this.font.getDescent();
			}
			return height;
		}
	}
	
	// Text and font //
	
	public boolean isTextReady() {
		return this.font != null && this.text != null && this.font.isValid();
	}
	
	protected void onFontChanged() { }
	
	public void removeFont() {
		this.font = null;
		this.updateBuffers = true;
		this.updateYOffset();
		this.onFontChanged();
	}
	
	public void setFont(Font font) {
		if (!font.isValid()) {
			throw new IllegalArgumentException("The given font is no longer valid.");
		} else if (this.font != font) {
			this.font = font;
			this.updateBuffers = true;
			// Update Y offset because the new font size can be used with auto height.
			this.updateYOffset();
			this.onFontChanged();
			// System.out.println("setFont new offsets: " + this);
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
	
	public float getCodePointOffset(int index) {
		if (this.codePointsOffsets == null || this.codePointsOffsets.length == 0 || index < 0) {
			return 0;
		} else if (index >= this.codePointsOffsets.length) {
			index = this.codePointsOffsets.length - 1;
		}
		return this.codePointsOffsets[index];
	}
	
	// Ignore descent //
	
	/**
	 * Set the "ignore descent" flag that can be used to ignore the font
	 * descent (numbers of pixels below the baseline) when used automatic
	 * height for this text. This is useful in some cases when you want
	 * to have a pretty alignement from borders of another object.
	 * @param ignoreDescent True to ignore font descent in auto height.
	 */
	public void setIgnoreDescent(boolean ignoreDescent) {
		if (this.ignoreDescent != ignoreDescent) {
			this.ignoreDescent = ignoreDescent;
			this.updateYOffset();
		}
	}
	
	// Effects //
	
	protected void addEffect(int at, SingleEffect effect) {
		if (this.effects == null) {
			this.effects = new HashMap<>();
		}
		this.effects.compute(at, (at_, current) -> (current == null) ? effect : current.groupWith(effect));
	}
	
	protected void removeEffect(int at, Class<? extends SingleEffect> effectClass) {
		if (this.effects != null) {
			this.effects.compute(at, (at_, current) -> (current == null) ? null : current.groupWithout(effectClass));
		}
	}
	
	protected void forEachEffect(int at, Consumer<SingleEffect> consumer) {
		if (this.effects != null) {
			Effect effect = this.effects.get(at);
			if (effect != null) {
				effect.forEach(consumer);
			}
		}
	}
	
	public void addColorEffect(int at, Color color) {
		this.addEffect(at, new ColorEffect(color));
	}
	
	public void removeColorEffect(int at) {
		this.removeEffect(at, ColorEffect.class);
	}
	
	// Rendering //
	
	private GuiBufferArray createTextBufferArray() {
		return this.getProgram(GuiProgramText.TYPE).createBuffer(true);
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
				
				// For a single glyph = 4 pos (8f), 4 tex (8f), 4 colors (16f=2*8f)
				temp.dataBuffer = MemoryUtil.memAllocFloat(temp.codePointsCount * 8 * 4);
				temp.indicesBuffer = MemoryUtil.memAllocInt(buf.setIndicesCount(temp.codePointsCount * 6));
				
			});
			
			TempBufferData bufferData;
			Glyph glyph;
			
			float[] pos = {0, this.font.getAscent()};
			Color[] color = {Color.WHITE};
			
			for (int i = 0, codePoint; i < codePointsCount; ++i) {
				
				this.forEachEffect(i, eff -> {
					if (eff.getClass() == ColorEffect.class) {
						color[0] = ((ColorEffect) eff).color;
					}
				});
				
				codePoint = this.codePoints[i];
				page = codePointsPages[i];
				
				bufferData = buffersCodePoints.get(page.getTextureName());
				glyph = page.getGlyph(codePoint);
				
				//System.out.print("Writing char '" + (char) codePoint + "': ");
				FloatBuffer dataBuffer = bufferData.dataBuffer;
				glyph.forEachCorner((gx, gy, tx, ty) -> {
					dataBuffer.put(pos[0] + gx).put(pos[1] + gy).put(tx).put(ty);
					//System.out.print((pos[0] + gx) + "/" + (pos[1] + gy) + "/" + tx + "/" + ty + "   ");
					color[0].putToBuffer(dataBuffer, true);
				});
				//System.out.println();
				
				GuiCommon.putSquareIndices(bufferData.indicesBuffer, bufferData.currentIndex);
				bufferData.currentIndex += 4;
				
				pos[0] += (i + 1 < codePointsCount) ? glyph.getKernAdvance(this.codePoints[i + 1]) : glyph.getAdvance();
				this.codePointsOffsets[i] = pos[0];
				
			}
			
			this.textWidth = pos[0];
			
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
			this.onTextBuffersRecomputed();
			
		} finally {
			
			buffersCodePoints.values().forEach(temp -> {
				if (temp.dataBuffer != null) MemoryUtil.memFree(temp.dataBuffer);
				if (temp.indicesBuffer != null) MemoryUtil.memFree(temp.indicesBuffer);
			});
			
		}
		
		this.updateBuffers = false;
		
	}
	
	protected void onTextBuffersRecomputed() { }
	
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
	
	// Effects //
	
	protected static abstract class Effect {
		protected abstract void forEach(Consumer<SingleEffect> consumer);
		protected abstract Effect groupWith(SingleEffect effect);
		protected abstract Effect groupWithout(Class<? extends SingleEffect> effectClass);
	}
	
	protected static class GroupEffect extends Effect {
		
		private final List<SingleEffect> effects = new ArrayList<>();
		
		@Override
		protected void forEach(Consumer<SingleEffect> consumer) {
			this.effects.forEach(consumer);
		}
		
		@Override
		protected Effect groupWith(SingleEffect effect) {
			for (int count = this.effects.size(), i = 0; i < count; ++i) {
				SingleEffect eff = this.effects.get(i);
				if (eff == effect) {
					// The effect already exists.
					return this;
				} else if (eff.getClass() == effect.getClass()) {
					this.effects.remove(i--);
				}
			}
			this.effects.add(effect);
			return this;
		}
		
		@Override
		protected Effect groupWithout(Class<? extends SingleEffect> effectClass) {
			for (int count = this.effects.size(), i = 0; i < count; ++i) {
				if (this.effects.get(i).getClass() == effectClass) {
					this.effects.remove(i--);
				}
			}
			// TODO: Maybe return the only one remaining item ? :
			// return this.effects.isEmpty() ? null : this.effects.size() == 1 ? this.effects.get(0) : this;
			return this.effects.isEmpty() ? null : this;
		}
		
	}
	
	protected static abstract class SingleEffect extends Effect {
		
		@Override
		protected void forEach(Consumer<SingleEffect> consumer) {
			consumer.accept(this);
		}
		
		@Override
		protected Effect groupWith(SingleEffect effect) {
			if (effect == this || effect.getClass() == this.getClass()) {
				return effect;
			} else {
				GroupEffect group = new GroupEffect();
				group.effects.add(this);
				group.effects.add(effect);
				return group;
			}
		}
		
		@Override
		protected Effect groupWithout(Class<? extends SingleEffect> effectClass) {
			return this.getClass() == effectClass ? null : this;
		}
		
	}
	
	protected static class ColorEffect extends SingleEffect {
		private final Color color;
		private ColorEffect(Color color) {
			this.color = color;
		}
	}
	
}
