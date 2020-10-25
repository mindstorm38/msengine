package io.msengine.client.graphics.font;

import io.msengine.client.util.BufferAlloc;
import io.msengine.common.asset.Asset;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBTruetype.*;

public class TrueTypeFont extends Font {

	public static TrueTypeFont fromRaw(ByteBuffer data, float fontHeight) throws IOException {
		
		STBTTFontinfo info = STBTTFontinfo.malloc();
		
		if (!stbtt_InitFont(info, data)) {
			throw new IOException("Failed to initialize font information.");
		}
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer ascent = stack.mallocInt(1);
			IntBuffer descent = stack.mallocInt(1);
			IntBuffer lineGap = stack.mallocInt(1);
			stbtt_GetFontVMetrics(info, ascent, descent, lineGap);
			return new TrueTypeFont(data, info, ascent.get(0), descent.get(0), lineGap.get(0), fontHeight);
		}
		
	}
	
	public static TrueTypeFont fromStream(InputStream stream, float fontHeight) throws IOException {
		return fromRaw(BufferAlloc.fromInputStream(stream), fontHeight);
	}
	
	public static TrueTypeFont fromAsset(Asset asset, float fontHeight) throws IOException {
		return fromStream(asset.openStreamExcept(), fontHeight);
	}
	
	// Class //
	
	private ByteBuffer data;
	private STBTTFontinfo info;
	private final int ascent;
	private final int descent;
	private final int lineGap;
	private final float fontHeight;
	
	private final List<GlyphPage> pages = new ArrayList<>();
	
	private TrueTypeFont(ByteBuffer data, STBTTFontinfo info, int ascent, int descent, int lineGap, float fontHeight) {
		this.data = data;
		this.info = info;
		this.ascent = ascent;
		this.descent = descent;
		this.lineGap = lineGap;
		this.fontHeight = fontHeight;
	}
	
	@Override
	public GlyphPage getGlyphPage(int codePoint) {
		
		int count = this.pages.size();
		GlyphPage page;
		for (int i = 0; i < count; ++i) {
			page = this.pages.get(i);
			if (page.hasCodePoint(codePoint)) {
				return page;
			} else if (codePoint < page.getRefCodePoint()) {
				
				final int bitmapSize = 512;
				final int maxGlyphsCount = 128;
				
				/*final int glyphsCount;
				
				if (i + 1 < count) {
					glyphsCount = Math.min(this.pages.get(i + 1).getRefCodePoint() - codePoint, maxGlyphsCount);
				} else {
					glyphsCount = maxGlyphsCount;
				}
				
				STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(maxGlyphsCount);
				ByteBuffer pixels = MemoryUtil.memAlloc(bitmapSize * bitmapSize);
				
				final int nextRefCodePoint;
				
				if (i + 1 < count) {
					nextRefCodePoint = this.pages.get(i + 1).getRefCodePoint();
				} else {
					nextRefCodePoint = 0;
				}
				
				int refCodePoint = codePoint;
				
				while (true) {
					
					int result = stbtt_BakeFontBitmap(this.data, this.fontHeight, pixels, bitmapSize, bitmapSize, refCodePoint, cdata);
					
					if (result > 0) {
						refCodePoint--;
					} else if (result < 0) {
						
						int fitCount = -result;
						break;
						
					} else {
					
					}
					
				}*/
				
			}
		}
		
		/*final int bitmapSize = 512;
		
		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		
		ByteBuffer pixels = MemoryUtil.memAlloc(bitmapSize * bitmapSize)
		stbtt_BakeFontBitmap(this.data, this.fontHeight, pixels, bitmapSize, bitmapSize, 0, cdata);*/
		
		return null;
		
	}
	
	@Override
	public void close() {
		
		if (this.data != null) {
			MemoryUtil.memFree(this.data);
			this.data = null;
		}
		
		if (this.info != null) {
			this.info.free();
			this.info = null;
		}
		
	}
	
}
