package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.graphics.gui.render.GuiProgramMain;
import io.msengine.client.graphics.texture.DynTexture2D;
import io.msengine.client.graphics.texture.MapTexture2D;
import io.msengine.client.graphics.texture.ResTexture2D;
import io.msengine.client.graphics.texture.base.Texture;
import io.msengine.client.graphics.texture.base.TextureSetup;
import io.msengine.client.util.BufferAlloc;
import io.msengine.common.asset.Asset;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class GuiTexture extends GuiObject {
	
	protected GuiBufferArray buf;
	protected boolean updateTexCoord;
	
	protected int textureName;
	protected float texCoordX, texCoordY;
	protected float texCoordW = 1f, texCoordH = 1f;
	protected float autoWidth, autoHeight;
	
	@Override
	protected void init() {
		this.buf = this.acquireProgram(GuiProgramMain.TYPE).createBufferSep(false, true);
		this.initBuffers();
	}
	
	@Override
	protected void stop() {
		this.releaseProgram(GuiProgramMain.TYPE);
		this.buf.close();
		this.buf = null;
	}
	
	@Override
	protected void render(float alpha) {
		
		if (this.textureName <= 0)
			return;
		
		if (this.updateTexCoord) {
			this.updateTexCoordsBuffer();
		}
		
		GuiProgramMain program = this.useProgram(GuiProgramMain.TYPE);
		
		this.model.push().translate(this.xIntOffset, this.yIntOffset).scale(this.realWidth, this.realHeight).apply();
		program.setTextureUnitAndBind(0, this.textureName);
		this.buf.draw();
		program.resetTextureUnitAndUnbind();
		this.model.pop();
		
	}
	
	@Override
	protected void update() { }
	
	@Override
	public float getAutoWidth() {
		if (this.isAutoHeight()) {
			return this.autoWidth;
		} else {
			return this.autoWidth * (this.realHeight / this.autoHeight);
		}
	}
	
	@Override
	public float getAutoHeight() {
		if (this.isAutoWidth()) {
			return this.autoHeight;
		} else {
			return this.autoHeight * (this.realWidth / this.autoWidth);
		}
	}
	
	@Override
	public void onRealWidthChanged() {
		super.onRealWidthChanged();
	}
	
	@Override
	public void onRealHeightChanged() {
		super.onRealHeightChanged();
	}
	
	private void initBuffers() {
		
		this.buf.bindVao();
		this.buf.allocateVboData(this.buf.getTexCoordBufIdx(), 8 << 2, BufferUsage.DYNAMIC_DRAW);
		
		BufferAlloc.allocStackInt(this.buf.setIndicesCount(6), buf -> {
			GuiCommon.putSquareIndices(buf);
			buf.flip();
			this.buf.uploadIboData(buf, BufferUsage.STATIC_DRAW);
		});
		
		BufferAlloc.allocStackFloat(8, buf -> {
			GuiCommon.putSquareVertices(buf, 1, 1);
			buf.flip();
			this.buf.bindVao();
			this.buf.uploadVboData(this.buf.getPositionIndex(), buf, BufferUsage.STATIC_DRAW);
		});
		
		this.updateTexCoordsBuffer();
		
	}
	
	private void updateTexCoordsBuffer() {
		
		BufferAlloc.allocStackFloat(8, buf -> {
			
			float x = this.texCoordX;
			float y = this.texCoordY;
			float w = this.texCoordW;
			float h = this.texCoordH;
			
			buf.put(x).put(y);
			buf.put(x).put(y + h);
			buf.put(x + w).put(y + h);
			buf.put(x + w).put(y);
			buf.flip();
			this.buf.bindVao();
			this.buf.uploadVboSubData(this.buf.getTexCoordBufIdx(), 0, buf);
			
		});
		
		this.updateTexCoord = false;
		
	}
	
	/** Remove the current internal texture, this will cause this object to be invisible */
	public void removeTexture() {
		this.textureName = 0;
	}
	
	/**
	 * Set the raw OpenGL texture name to use for this texture element.
	 * @param name OpenGL texture name.
	 */
	public void setTexture(int name) {
		this.textureName = name;
	}
	
	/**
	 * Set the internal texture name from a {@link Texture} wrapper.
	 * If the wrapper is an instance of {@link DynTexture2D} or a
	 * {@link ResTexture2D} then the auto width and height of this
	 * texture are set according to <code>getWidth()</code> and
	 * <code>getHeight()</code> if the given texture.
	 * @param texture The texture wrapper (must be internally valid).
	 */
	public void setTexture(Texture texture) {
		
		texture.checkValid();
		this.setTexture(texture.getName());
		
		if (texture instanceof DynTexture2D) {
			this.autoWidth = ((DynTexture2D) texture).getWidth();
			this.autoHeight = ((DynTexture2D) texture).getHeight();
			this.updateOffsets();
		} else if (texture instanceof ResTexture2D) {
			this.autoWidth = ((ResTexture2D) texture).getWidth();
			this.autoHeight = ((ResTexture2D) texture).getHeight();
			this.updateOffsets();
		}
		
	}
	
	public void setTextureCoords(float x, float y, float width, float height) {
		this.texCoordX = x;
		this.texCoordY = y;
		this.texCoordW = width;
		this.texCoordH = height;
		this.updateTexCoord = true;
	}
	
	public void resetTextureCoords() {
		this.setTextureCoords(0, 0, 1, 1);
	}
	
	public void setTextureFull(int name) {
		this.setTexture(name);
		this.resetTextureCoords();
	}
	
	public void setTextureFull(Texture texture) {
		this.setTexture(texture);
		this.resetTextureCoords();
	}
	
	public void setTextureTile(MapTexture2D.Tile tile) {
		MapTexture2D map = tile.getMap();
		map.checkValid();
		this.setTexture(map.getName());
		this.setTextureCoords(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
		this.autoWidth = map.getWidth() * tile.getWidth();
		this.autoHeight = map.getHeight() * tile.getHeight();
	}
	
	@Override
	protected void buildToString(StringBuilder builder) {
		super.buildToString(builder);
		builder.append(", tex=").append(this.textureName);
		builder.append(", texCoord=").append(this.texCoordX).append('/').append(this.texCoordY);
		builder.append(", texSize=").append(this.texCoordW).append('/').append(this.texCoordH);
	}
	
	public static class Simple extends GuiTexture {
		
		private final TextureSetup textureSetup;
		private final Asset asset;
		private ResTexture2D tex;
		
		public Simple(TextureSetup textureSetup, Asset asset) {
			this.textureSetup = Objects.requireNonNull(textureSetup);
			this.asset = asset;
		}
		
		public Simple(Asset asset) {
			this(Texture.SETUP_LINEAR, asset);
		}
		
		@Override
		protected void init() {
			super.init();
			if (this.asset != null) {
				try {
					this.tex = new ResTexture2D(this.textureSetup, this.asset);
					this.setTexture(this.tex);
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Failed to load texture.", e);
				}
			} else {
				LOGGER.warning("No asset for this texture.");
			}
		}
		
		@Override
		protected void stop() {
			super.stop();
			this.removeTexture();
			if (this.tex != null) {
				this.tex.close();
				this.tex = null;
			}
		}
		
	}
	
}
