package io.msengine.client.graphics.font.truetype;

import io.msengine.client.graphics.font.Font;
import io.msengine.client.graphics.font.FontTexture2D;
import io.msengine.client.graphics.font.Glyph;
import io.msengine.client.graphics.font.GlyphPage;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;

public class TrueTypeFont extends Font {
	
	private final float scale;
	private final float ascent;
	private final float descent;
	private final float lineGap;
	
	private final List<GlyphPage> pages = new ArrayList<>();
	
	TrueTypeFont(TrueTypeFontFamily family, float size, float scale, float ascent, float descent, float lineGap) {
		super(family, size);
		this.scale = scale;
		this.ascent = ascent;
		this.descent = descent;
		this.lineGap = lineGap;
	}
	
	@Override
	public TrueTypeFontFamily getFamily() {
		return (TrueTypeFontFamily) super.getFamily();
	}
	
	public float getScale() {
		return this.scale;
	}
	
	@Override
	public GlyphPage getGlyphPage(int codePoint) {
		
		this.checkValid();
		
		int pagesCount = this.pages.size();
		GlyphPage page = null;
		
		for (int i = 0; i < pagesCount; ++i) {
			
			page = this.pages.get(i);
			
			if (page.hasCodePoint(codePoint)) {
				return page;
			} else if (codePoint < page.getRefCodePoint()) {
				
				int minCodePoint = (i == 0) ? 0 : (this.pages.get(i - 1).getLastCodePoint() + 1);
				GlyphPage newPage = this.buildGlyphPage(codePoint, minCodePoint, page.getRefCodePoint() - 1);
				this.pages.add(i, newPage);
				return newPage;
				
			}
			
		}
		
		int minCodePoint = (page == null) ? 0 : (page.getLastCodePoint() + 1);
		GlyphPage newPage = this.buildGlyphPage(codePoint, minCodePoint, Character.MAX_CODE_POINT);
		this.pages.add(newPage);
		return newPage;
		
	}
	
	private GlyphPage buildGlyphPage(final int codePoint, final int minCodePoint, final int maxCodePoint) {
		
		// Precondition :
		//  minCodePoint <= codePoint <= maxCodePoint
		
		TrueTypeFontFamily family = this.getFamily();
		
		System.out.println("### buildGlyphPage for codePoint=" + codePoint + ", min=" + minCodePoint + ", max=" + maxCodePoint);
		
		// It's useless to have more maximum code points than the maximum min->max range.
		final int codePoints = Math.min(128, maxCodePoint - minCodePoint + 1);
		final int rawBitmapSize = (int) (this.getSize() * 6); // TODO: Maybe fix the UNPACK_ALIGNMENT parameter instead ?
		final int bitmapSize = rawBitmapSize + ((4 - (rawBitmapSize & 3)) & 3); // Padded for texture store
		
		System.out.println("codePoints=" + codePoints);
		System.out.println("bitmapSize=" + bitmapSize);
		
		// Compute refCodePoint by subtracting half of codePoints to wanted code point.
		int refCodePoint = codePoint - (codePoints >> 1);
		System.out.println("refCodePoint=" + refCodePoint);
		
		// If refCodePoint if less than minCodePoint, clamp.
		if (refCodePoint < minCodePoint) {
			System.out.println("refCodePoint < minCodePoint : refCodePoint=" + minCodePoint);
			refCodePoint = minCodePoint;
		}
		
		// Compute lastCodePoint by adding codePoints-1 to ref.
		int lastCodePoint = refCodePoint + codePoints - 1;
		System.out.println("lastCodePoint=" + lastCodePoint);
		
		// If lastCodePoint is greater than maxCodePoint, clamp.
		if (lastCodePoint > maxCodePoint) {
			System.out.println("lastCodePoint > maxCodePoint : lastCodePoint=" + maxCodePoint);
			lastCodePoint = maxCodePoint;
		}
		
		// Recompute refCodePoint to ensure maximum codePoints at once.
		refCodePoint = lastCodePoint - codePoints + 1;
		System.out.println("refCodePoint=" + refCodePoint);
		
		// Allocate pixels and char data
		ByteBuffer pixels = MemoryUtil.memAlloc(bitmapSize * bitmapSize);
		STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(codePoints);
		
		int bitmapHeight = bitmapSize;
		
		while (true) {
			
			int res = stbtt_BakeFontBitmap(family.getData(), this.getSize(), pixels, bitmapSize, bitmapSize, refCodePoint, charData);
			
			System.out.println("res=" + res);
			
			if (res >= 0) {
				bitmapHeight = res;
				break;
			} else {
				
				int bakedCodePoints = -res;
				int bakedLastCodePoint = refCodePoint + bakedCodePoints - 1;
				System.out.println("bakedLastCodePoint=" + bakedLastCodePoint);
				
				if (bakedLastCodePoint < codePoint) {
					
					System.out.println("bakedLastCodePoint < codePoint");
					
					// Increase refCodePoint, but decrease charData limit by one.
					refCodePoint++;
					
					// Decrease charData limit by one.
					int cap = charData.limit();
					charData.clear();
					charData.limit(cap - 1);
					
				} else {
					
					System.out.println("bakedLastCodePoint >= codePoint");
					
					if (charData.limit() == bakedLastCodePoint) {
						break;
					}
					
					// Clear and set limit, in order to recompute the last unused row.
					charData.clear();
					charData.limit(bakedCodePoints);
					
				}
				
				// Clear pixels for next bake.
				pixels.clear();
				
			}
			
		}
		
		/*for (int y = 0; y < bitmapHeight; ++y) {
			for (int x = 0; x < bitmapSize; ++x) {
				System.out.print(pixels.get(y * bitmapSize + x) == 0 ? ' ' : 'o');
			}
			System.out.println();
		}*/
		
		System.out.println("bitmapHeight=" + bitmapHeight);
		
		if (bitmapHeight == 0) {
			MemoryUtil.memFree(pixels);
			charData.free();
			return null;
		}
		
		FontTexture2D tex = new FontTexture2D(pixels, bitmapSize, bitmapHeight);
		MemoryUtil.memFree(pixels);
		
		Glyph[] glyphs = new Glyph[charData.remaining()];
		float scale = this.scale;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			STBTTBakedChar bc;
			
			STBTTAlignedQuad aq = STBTTAlignedQuad.mallocStack(stack);
			FloatBuffer xPos = stack.mallocFloat(1);
			FloatBuffer yPos = stack.mallocFloat(1);
			
			for (int i = 0; i < glyphs.length; ++i) {
				
				bc = charData.get(i);
				xPos.put(0, 0);
				yPos.put(0, 0);
				
				stbtt_GetBakedQuad(charData, bitmapSize, bitmapHeight, i, xPos, yPos, aq, true);
				
				glyphs[i] = new TrueTypeGlyph(
						this,
						refCodePoint + i,
						aq.s0(), aq.t0(),
						aq.s1(), aq.t1(),
						aq.x0(), aq.y0(),
						aq.x1(), aq.y1(),
						bc.xadvance()
				);
				
			}
			
		}
		
		charData.free();
		
		return new GlyphPage(tex, refCodePoint, glyphs);
		
	}
	
	@Override
	public void close() {
		this.pages.forEach(page -> page.getTexture().close());
		this.pages.clear();
	}
	
}
