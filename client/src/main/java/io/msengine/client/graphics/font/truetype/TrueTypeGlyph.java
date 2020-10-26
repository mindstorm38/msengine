package io.msengine.client.graphics.font.truetype;

import io.msengine.client.graphics.font.glyph.Glyph;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance;

public class TrueTypeGlyph extends Glyph {
	
	private final TrueTypeFont font;
	
	public TrueTypeGlyph(TrueTypeFont font, int codePoint, float tx0, float ty0, float tx1, float ty1, float px0, float py0, float px1, float py1, float advance) {
		super(codePoint, tx0, ty0, tx1, ty1, px0, py0, px1, py1, advance);
		this.font = font;
	}
	
	@Override
	public float getKernAdvance(int nextCodePoint) {
		return this.getAdvance() + (stbtt_GetCodepointKernAdvance(this.font.getFamily().getInfo(), this.getCodePoint(), nextCodePoint) * this.font.getScale());
	}
	
}
