package io.msengine.client.graphics.texture;

import io.msengine.common.asset.Asset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class ResTexture extends Texture {

	public ResTexture() {
		super(GL_TEXTURE_2D);
	}

	public ResTexture(InputStream stream) throws IOException {
		this();
		this.uploadImageFromStream(stream);
	}

	public ResTexture(Asset asset) throws IOException {
		this();
		this.uploadImageFromAsset(asset);
	}

	public void uploadImageRaw(ByteBuffer buf, int width, int height) {
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
	}

	public void uploadImageFromStream(InputStream stream) throws IOException {
		loadImageFromStream(stream, 8192, this::uploadImageRaw);
	}

	public void uploadImageFromAsset(Asset asset) throws IOException {
		this.uploadImageFromStream(asset.openStreamExcept());
	}

}
