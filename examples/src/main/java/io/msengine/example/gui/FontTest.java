package io.msengine.example.gui;

import io.msengine.client.graphics.texture.ResTexture2D;
import io.msengine.client.graphics.texture.base.Texture;
import io.msengine.client.graphics.texture.base.Texture2D;
import io.msengine.client.util.BufferAlloc;
import io.msengine.common.asset.Asset;
import io.msengine.common.asset.Assets;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_R8;
import static org.lwjgl.stb.STBTruetype.*;

public class FontTest {
	
	private static final Assets ASSETS = Assets.forClass(GuiTestScene.class, "assets");
	private static final Asset UBUNTU_REGULAR_FONT = ASSETS.getAsset("mseex/Ubuntu-Regular.ttf");
	
	public static ResTexture2D test(float textHeight) {
		
		ByteBuffer data;
		
		try {
			data = BufferAlloc.fromInputStream(UBUNTU_REGULAR_FONT.openStreamExcept());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		STBTTFontinfo info = STBTTFontinfo.malloc();
		
		if (!stbtt_InitFont(info, data)) {
			System.out.println("Failed to initialize.");
			return null;
		}
		
		final int ascent, descent, lineGap;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer bAscent = stack.mallocInt(1);
			IntBuffer bDescent = stack.mallocInt(1);
			IntBuffer bLineGap = stack.mallocInt(1);
			stbtt_GetFontVMetrics(info, bAscent, bDescent, bLineGap);
			ascent = bAscent.get(0);
			descent = bDescent.get(0);
			lineGap = bLineGap.get(0);
			System.out.println("ascent=" + ascent + ", descent=" + descent + ", lineGap=" + lineGap);
		}
		
		float scale = stbtt_ScaleForPixelHeight(info, textHeight);
		System.out.println("scale=" + scale);
		System.out.println("<scaled> ascent=" + (scale * ascent) + ", descent=" + (scale * descent) + ", lineGap=" + (scale * lineGap));
		
		int bitmapWidth = (int) (textHeight * 6);
		int bitmapHeight = (int) (textHeight * 6);
		
		ByteBuffer pixels = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
		STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(128);
		
		final int firstChar = 30;
		int res = stbtt_BakeFontBitmap(data, textHeight, pixels, bitmapWidth, bitmapHeight, firstChar, charData);
		int charCount;
		
		if (res > 0) {
			bitmapHeight = res;
			charCount = charData.capacity();
		} else {
			charCount = -res;
		}
		
		System.out.println("charCount=" + charCount + ", size=" + bitmapWidth + "/" + bitmapHeight);
		
		/*int finalCharCount;
		int finalBitmapHeight;
		
		while (true) {
			
			pixels.clear();
			charData.clear();
			
			int res = stbtt_BakeFontBitmap(data, textHeight, pixels, bitmapSize, bitmapSize, firstChar, charData);
			System.out.println("res=" + res + ", cap=" + charData.capacity());
			
			if (res > 0) {
				int charDataCap = charData.capacity();
				charData.free();
				charData = STBTTBakedChar.malloc(charDataCap << 1);
			} else if (res < 0) {
				finalCharCount = -res;
				finalBitmapHeight =
				break;
			} else {
				finalCharCount = 0;
				finalBitmapHeight = 0;
				break;
			}
			
		}
		
		System.out.println("finalCharCount=" + finalCharCount);
		System.out.println("finalBitmapHeight=" + finalBitmapHeight);*/
		
		/*System.out.println("char data:");
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			IntBuffer x0 = stack.mallocInt(1);
			IntBuffer y0 = stack.mallocInt(1);
			IntBuffer x1 = stack.mallocInt(1);
			IntBuffer y1 = stack.mallocInt(1);
			
			//STBTTAlignedQuad aq = STBTTAlignedQuad.mallocStack(stack);
			
			for (int i = 0; i < charData.capacity(); ++i) {
				
				int codePoint = firstChar + i;
				
				// STBTTBakedChar bc = charData.get(i);
				System.out.print("'" + ((char) codePoint) + "':");
				
				if (stbtt_GetCodepointBox(info, codePoint, x0, y0, x1, y1)) {
					System.out.println("left=" + (scale * x0.get(0)) + ", bottom=" + (scale * y0.get(0)) + ", right=" + (scale * x1.get(0)) + ", top=" + (scale * y1.get(0)));
				} else {
					System.out.println("err");
				}
				
				//stbtt_GetBakedQuad(charData, bitmapSize, bitmapSize, i, xPos, yPos, aq, true);
				
				//System.out.println(((char) (firstChar + i)) + " : 0=" + bc.x0() + "/" + bc.y0() + " 1=" + bc.x1() + "/" + bc.y1() + " off=" + bc.xoff() + "/" + bc.yoff() + " adv=" + bc.xadvance());
				//System.out.println("'" + ((char) (firstChar + i)) + "': p0=" + aq.x0() + "/" + aq.y0() + ", p1=" + aq.x1() + "/" + aq.y1() + ", t0=" + aq.s0() + "/" + aq.t0() + ", t1=" + aq.s1() + "/" + aq.t1());
				//System.out.println(" => " + xPos.get(0) + "/" + yPos.get(0));
				
			}
			
		}*/
		
		ResTexture2D tex = new ResTexture2D(Texture.SETUP_LINEAR_KEEP);
		tex.uploadImageRaw(pixels, GL_RED, GL_UNSIGNED_BYTE, bitmapWidth, bitmapHeight, GL_R8);
		tex.unbind();
		return tex;
		
	}
	
}
