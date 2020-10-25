package io.msengine.client.graphics.font.truetype;

import io.msengine.client.graphics.font.Font;
import io.msengine.client.graphics.font.FontFamily;
import io.msengine.client.util.BufferAlloc;
import io.msengine.common.asset.Asset;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBTruetype.STBTT_MS_LANG_ENGLISH;
import static org.lwjgl.stb.STBTruetype.STBTT_PLATFORM_ID_UNICODE;
import static org.lwjgl.stb.STBTruetype.STBTT_UNICODE_EID_UNICODE_1_0;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontNameString;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;

public class TrueTypeFontFamily extends FontFamily {
	
	private ByteBuffer data;
	private STBTTFontinfo info;
	private final String name;
	private final int ascent;
	private final int descent;
	private final int lineGap;
	
	public TrueTypeFontFamily(ByteBuffer data) throws IOException {
		
		STBTTFontinfo info = STBTTFontinfo.malloc();
		
		if (!stbtt_InitFont(info, data)) {
			throw new IOException("Failed to initialize font information.");
		}
		
		this.data = data;
		this.info = info;
		
		// See: https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6name.html
		// FIXME: Currently not working
		ByteBuffer nameString = stbtt_GetFontNameString(info, STBTT_PLATFORM_ID_UNICODE, STBTT_UNICODE_EID_UNICODE_1_0, STBTT_MS_LANG_ENGLISH, 1);
		this.name = (nameString != null) ? MemoryUtil.memUTF8(nameString) : "unknown";
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer ascent = stack.mallocInt(1);
			IntBuffer descent = stack.mallocInt(1);
			IntBuffer lineGap = stack.mallocInt(1);
			stbtt_GetFontVMetrics(info, ascent, descent, lineGap);
			this.ascent = ascent.get(0);
			this.descent = descent.get(0);
			this.lineGap = lineGap.get(0);
		}
		
	}
	
	public TrueTypeFontFamily(InputStream stream) throws IOException {
		this(BufferAlloc.fromInputStream(stream));
	}
	
	public TrueTypeFontFamily(Asset asset) throws IOException {
		this(asset.openStreamExcept());
	}
	
	ByteBuffer getData() {
		return this.data;
	}
	
	STBTTFontinfo getInfo() {
		return this.info;
	}
	
	private void checkNotClosed() {
		if (this.info == null || this.data == null) {
			throw new IllegalStateException("Can't use this font family because ");
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	protected Font buildFontForSize(float size) {
		float scale = stbtt_ScaleForPixelHeight(this.info, size);
		return new TrueTypeFont(this, size, scale, scale * this.ascent, scale * this.descent, scale * this.lineGap);
	}
	
	@Override
	public void close() {
		
		super.close();
		
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
