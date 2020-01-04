package io.msengine.client.renderer.util;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import io.msengine.client.renderer.framebuffer.Framebuffer;
import io.msengine.client.util.Utils;

/**
 * Helper to take screenshots from a {@link Framebuffer}.
 */
public class ScreenshotHelper {
	
	/**
	 * Take a screenshot an return a {@link BufferedImage} taken from a {@link Framebuffer} region.
	 * @param framebuffer The framebuffer.
	 * @param x The start x coordinates on the Framebuffer.
	 * @param y The start y coordinates on the Framebuffer.
	 * @param width The width of the capture on the Framebuffer.
	 * @param height The height of the capture on the Framebuffer.
	 * @return The screenshot image.
	 */
	public static BufferedImage takeScreenshot(Framebuffer framebuffer, int x, int y, int width, int height) {
			
		if ( framebuffer == null ) Framebuffer.unbind();
		else framebuffer.bind( true );
		
		ByteBuffer pixels = null;
		
		try {
			
			pixels = MemoryUtil.memAlloc( width * height * 4 );
			
			glReadPixels( x, y, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels );
			
			return Utils.getBufferImage( pixels, width, height );
			
		} finally {
			
			if ( pixels != null ) MemoryUtil.memFree( pixels );
			
		}
		
	}
	
	public static BufferedImage takeScreenshot(Framebuffer framebuffer) {
		return takeScreenshot( framebuffer, 0, 0, framebuffer.getWidth(), framebuffer.getHeight() );
	}
	
}
