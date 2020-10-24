package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.texture.base.Texture2D;
import io.msengine.client.graphics.texture.base.TextureSetup;
import io.msengine.client.graphics.util.ImageUtils;
import io.msengine.common.asset.Asset;
import io.msengine.common.util.Color;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class DynTexture2D extends Texture2D {

	private ByteBuffer buf;
	private int width, height;

	public DynTexture2D(TextureSetup setup) {
		super(setup);
	}

	public DynTexture2D(TextureSetup setup, int width, int height) {
		this(setup);
		this.allocImage(width, height);
	}

	public DynTexture2D(TextureSetup setup, InputStream stream, boolean upload) throws IOException {
		this(upload ? setup.withUnbind(false) : setup);
		this.allocFromStream(stream);
		if (upload) {
			this.uploadImage();
			setup.unbind(this);
		}
	}

	public DynTexture2D(TextureSetup setup, Asset asset, boolean upload) throws IOException {
		this(upload ? setup.withUnbind(false) : setup);
		this.allocFromAsset(asset);
		if (upload) {
			this.uploadImage();
			setup.unbind(this);
		}
	}
	
	public DynTexture2D() {
		this(SETUP_LINEAR);
	}
	
	public DynTexture2D(int width, int height) {
		this(SETUP_LINEAR, width, height);
	}
	
	public DynTexture2D(InputStream stream, boolean upload) throws IOException {
		this(upload ? SETUP_LINEAR_KEEP : SETUP_LINEAR, stream, upload);
	}
	
	public DynTexture2D(Asset asset, boolean upload) throws IOException {
		this(upload ? SETUP_LINEAR_KEEP : SETUP_LINEAR, asset, upload);
	}
	
	// Edition //

	/**
	 * @return The internal buffer, you must not free the buffer.
	 */
	public ByteBuffer getBuffer() {
		return this.buf;
	}

	/**
	 * @return The image width of the image stored in internal buffer.
	 * @see #getBuffer()
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @return The image height of the image stored in internal buffer.
	 * @see #getBuffer()
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Free the image internal buffer, reset its size to 0.
	 */
	public void freeImage() {
		if (this.buf != null) {
			MemoryUtil.memFree(this.buf);
			this.buf = null;
			this.width = 0;
			this.height = 0;
		}
	}

	/**
	 * <p><b>UNSAFE!</b> Set inner buffer with its image size.</p>
	 * @param buf The buffer.
	 * @param width Image width.
	 * @param height Image height.
	 */
	protected void setRawBuffer(ByteBuffer buf, int width, int height) {
		if (this.buf != null) {
			MemoryUtil.memFree(this.buf);
		}
		this.buf = buf;
		this.width = width;
		this.height = height;
	}

	/**
	 * <p>Allocate the image internal buffer for specified width and height,
	 * the buffer is not always the exact needed size to avoid abusive
	 * reallocation.</p>
	 * <p>Reallocation happen in two cases:<br>
	 * 1. The needed capacity is larger than the previous one.<br>
	 * 2. The needed capacity is two times smaller than the previous one.</p>
	 * @param width The image width.
	 * @param height The image height.
	 */
	public void allocImage(int width, int height) {

		if (width < 1 || height < 1) {
			throw new IllegalArgumentException("Invalid width or height, should not be less than 1.");
		}

		int size = width * height * 4;

		if (this.buf == null) {
			this.buf = MemoryUtil.memAlloc(size);
		} else if (size > this.buf.capacity() || size < (this.buf.capacity() << 2)) {
			this.buf = MemoryUtil.memRealloc(this.buf, size);
		} else {
			this.buf.clear();
			this.buf.limit(size);
		}

		this.width = width;
		this.height = height;

	}

	public void uploadImage() {
		if (this.buf == null) {
			throw new IllegalStateException("Can't upload this dynamic texture because it was not allocated.");
		} else {
			this.checkBound();
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, this.buf);
		}
	}

	public void allocFromStream(InputStream stream) throws IOException {
		ImageUtils.loadImageFromStream(stream, 8192, this::setRawBuffer);
	}

	public void allocFromAsset(Asset asset) throws IOException {
		this.allocFromStream(Objects.requireNonNull(asset, "Asset is null.").openStreamExcept());
	}

	// Graphics //

	protected void checkPixelCoord(int x, int y) {
		if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
			throw new IllegalArgumentException("The pixel coordinate is out of the image bounds.");
		}
	}

	protected int getPixelIndex(int x, int y) {
		return (x + y * this.width) * 4;
	}

	protected void setPixelRaw(int x, int y, int red, int green, int blue, int alpha) {
		int idx = this.getPixelIndex(x, y);
		this.buf.put(idx, (byte) red);
		this.buf.put(idx + 1, (byte) green);
		this.buf.put(idx + 2, (byte) blue);
		this.buf.put(idx + 3, (byte) alpha);
	}

	public void setPixel(int x, int y, int red, int green, int blue, int alpha) {
		this.checkPixelCoord(x, y);
		this.setPixelRaw(x, y, red, green, blue, alpha);
	}

	public void setPixel(int x, int y, int red, int green, int blue, float alpha) {
		this.setPixel(x, y, red, green, blue, (byte) (alpha * 255));
	}

	public void setPixel(int x, int y, float red, float green, float blue, float alpha) {
		this.setPixel(x, y, (byte) (red * 255), (byte) (green * 255), (byte) (blue * 255), (byte) (alpha * 255));
	}

	public void setPixel(int x, int y, Color color) {
		this.setPixel(x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public void fillPixels(int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		for (int ox = 0; ox < width; ++ox) {
			for (int oy = 0; oy < height; ++oy) {
				setPixelRaw(x + ox, y + oy, red, green, blue, alpha);
			}
		}
	}

	public void fillPixels(int x, int y, int width, int height, int red, int green, int blue, float alpha) {
		this.fillPixels(x, y, width, height, red, green, blue, (byte) (alpha * 255));
	}

	public void fillPixels(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
		this.fillPixels(x, y, width, height, (byte) (red * 255), (byte) (green * 255), (byte) (blue * 255), (byte) (alpha * 255));
	}

	public void fillPixels(int x, int y, int width, int height, Color color) {
		this.fillPixels(x, y, width, height, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	@Override
	public void close() {
		super.close();
		this.freeImage();
	}

}
