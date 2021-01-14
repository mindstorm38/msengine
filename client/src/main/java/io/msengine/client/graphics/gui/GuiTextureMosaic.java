package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.texture.ResTexture2D;
import io.msengine.client.graphics.texture.base.Texture;
import io.msengine.client.graphics.texture.base.TextureSetup;
import io.msengine.common.asset.Asset;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class GuiTextureMosaic extends GuiTexture {
	
	protected float tileWidth = 1f;
	protected float tileHeight = 1f;
	
	protected float mosaicOffsetX = 0f;
	protected float mosaicOffsetY = 0f;
	
	@Override
	public void onRealWidthChanged() {
		super.onRealWidthChanged();
		this.updateTexCoordX();
	}
	
	@Override
	public void onRealHeightChanged() {
		super.onRealHeightChanged();
		this.updateTexCoordY();
	}
	
	@Override
	public void resetTextureCoords() {
		throw new UnsupportedOperationException("Unsupported method for GuiTextureMosaic, textures coords are computed apart.");
	}
	
	// Tile size //
	
	public float getTileWidth() {
		return tileWidth;
	}
	
	public void setTileWidth(float tileWidth) {
		this.tileWidth = tileWidth;
		this.updateTexCoordX();
	}
	
	public float getTileHeight() {
		return tileHeight;
	}
	
	public void setTileHeight(float tileHeight) {
		this.tileHeight = tileHeight;
		this.updateTexCoordY();
	}
	
	public void setTileSize(float width, float height) {
		this.setTileWidth(width);
		this.setTileHeight(height);
	}
	
	// Mosaic offset //
	
	public float getMosaicOffsetX() {
		return mosaicOffsetX;
	}
	
	public void setMosaicOffsetX(float mosaicOffsetX) {
		this.mosaicOffsetX = mosaicOffsetX;
		this.updateTexCoordX();
	}
	
	public float getMosaicOffsetY() {
		return mosaicOffsetY;
	}
	
	public void setMosaicOffsetY(float mosaicOffsetY) {
		this.mosaicOffsetY = mosaicOffsetY;
		this.updateTexCoordY();
	}
	
	public void setMosaicOffset(float xOffset, float yOffset) {
		this.setMosaicOffsetX(xOffset);
		this.setMosaicOffsetY(yOffset);
	}
	
	// Update coords //
	
	private void updateTexCoordX() {
		
		this.texCoordW = this.getRealWidth() / this.tileWidth;
		this.texCoordX = (this.texCoordW / -2f) + this.mosaicOffsetX;
		this.updateTexCoord = true;
		
	}
	
	private void updateTexCoordY() {
		
		this.texCoordH = this.getRealHeight() / this.tileHeight;
		this.texCoordY = (this.texCoordH / -2f) + this.mosaicOffsetY;
		this.updateTexCoord = true;
		
	}
	
	@Override
	protected void buildToString(StringBuilder builder) {
		super.buildToString(builder);
		builder.append(", tileSize=").append(this.tileWidth).append('/').append(this.tileHeight);
		builder.append(", mosaicOffset=").append(this.mosaicOffsetX).append('/').append(this.mosaicOffsetY);
	}
	
	public static class Simple extends GuiTextureMosaic {
		
		private final TextureSetup textureSetup;
		private final Asset asset;
		private ResTexture2D tex;
		
		public Simple(TextureSetup textureSetup, Asset asset) {
			this.asset = Objects.requireNonNull(asset);
			this.textureSetup = Objects.requireNonNull(textureSetup);
		}
		
		public Simple(Asset asset) {
			this(Texture.SETUP_LINEAR, asset);
		}
		
		@Override
		protected void init() {
			super.init();
			try {
				this.tex = new ResTexture2D(this.textureSetup, this.asset);
				this.setTexture(this.tex);
				this.setTileSize(this.tex.getWidth(), this.tex.getHeight());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Failed to load texture.", e);
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
