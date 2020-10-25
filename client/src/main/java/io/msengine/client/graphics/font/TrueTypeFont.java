package io.msengine.client.graphics.font;

import io.msengine.client.util.BufferAlloc;
import io.msengine.common.asset.Asset;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

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
	
	private final ByteBuffer data;
	private final STBTTFontinfo info;
	private final int ascent;
	private final int descent;
	private final int lineGap;
	private final float fontHeight;
	
	private TrueTypeFont(ByteBuffer data, STBTTFontinfo info, int ascent, int descent, int lineGap, float fontHeight) {
		this.data = data;
		this.info = info;
		this.ascent = ascent;
		this.descent = descent;
		this.lineGap = lineGap;
		this.fontHeight = fontHeight;
	}
	
	@Override
	public GlyphPage getGlyphPage(int codepoint) {
		
		/*final int bitmapSize = 1024;
		
		STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
		
		ByteBuffer pixels = MemoryUtil.memAlloc(bitmapSize * bitmapSize)
		stbtt_BakeFontBitmap(this.data, this.fontHeight, pixels, bitmapSize, bitmapSize, 0, cdata);*/
		
		return null;
		
	}
	
	@Override
	public void close() {
		MemoryUtil.memFree(this.data);
		this.info.free();
	}
	
}
