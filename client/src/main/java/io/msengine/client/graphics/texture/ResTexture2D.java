package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.texture.base.Texture2D;
import io.msengine.client.graphics.texture.base.TextureSetup;
import io.msengine.client.graphics.util.ImageUtils;
import io.msengine.common.asset.Asset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class ResTexture2D extends Texture2D {
	
	private int width, height;
	
	public ResTexture2D(TextureSetup setup) {
		super(setup);
	}

	public ResTexture2D(TextureSetup setup, InputStream stream) throws IOException {
		this(setup.withUnbind(false));
		this.uploadImageFromStream(stream);
		setup.unbind(this);
	}

	public ResTexture2D(TextureSetup setup, Asset asset) throws IOException {
		this(setup.withUnbind(false));
		this.uploadImageFromAsset(asset);
		setup.unbind(this);
	}
	
	public ResTexture2D() {
		this(SETUP_LINEAR);
	}
	
	public ResTexture2D(InputStream stream) throws IOException {
		this(SETUP_LINEAR_KEEP, stream);
	}
	
	public ResTexture2D(Asset asset) throws IOException {
		this(SETUP_LINEAR_KEEP, asset);
	}
	
	// Last size //
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	// Upload //
	
	public void uploadImageRaw(ByteBuffer buf, int width, int height) {
		this.checkBound();
		this.width = width;
		this.height = height;
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
	}
	
	public void uploadImageFromStream(InputStream stream) throws IOException {
		this.checkBound();
		ImageUtils.loadImageFromStream(stream, 8192, this::uploadImageRaw);
	}
	
	public void uploadImageFromAsset(Asset asset) throws IOException {
		this.uploadImageFromStream(Objects.requireNonNull(asset, "Asset is null.").openStreamExcept());
	}

}
