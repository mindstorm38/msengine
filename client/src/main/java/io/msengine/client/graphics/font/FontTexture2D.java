package io.msengine.client.graphics.font;

import io.msengine.client.graphics.texture.ResTexture2D;
import io.msengine.client.graphics.texture.base.Texture;
import io.msengine.client.graphics.texture.base.TextureSetup;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30.GL_R8;

public class FontTexture2D extends ResTexture2D {
	
	public FontTexture2D(TextureSetup setup, ByteBuffer buf, int width, int height) {
		super(setup.withUnbind(false));
		this.uploadImageRaw(buf, GL_RED, GL_UNSIGNED_BYTE, width, height, GL_R8);
		this.unbind();
	}
	
	public FontTexture2D(ByteBuffer buf, int width, int height) {
		this(Texture.SETUP_LINEAR_KEEP, buf, width, height);
	}
	
}
