package io.msengine.client.gui;

import io.msengine.client.renderer.texture.TextureMapTile;

/**
 *
 * A special texture to make mosaics with a specific tile size.
 *
 * @author Theo Rozier
 *
 */
public class GuiTextureMosaic extends GuiTexture {

	protected float tileWidth = 1f;
	protected float tileHeight = 1f;
	
	protected float mosaicOffsetX = 0f;
	protected float mosaicOffsetY = 0f;
	
	@Override
	public float getAutoWidth() {
		return this.tileWidth;
	}
	
	@Override
	public float getAutoHeight() {
		return this.tileHeight;
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.updateTexCoordX();
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.updateTexCoordY();
	}
	
	@Override
	public void setTexture(TextureMapTile textureMapTile) {
		throw new UnsupportedOperationException("Can't use texture map tile in a GuiTextureMosaic.");
	}
	
	@Override
	public void resetCoordinates() {}
	
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
	
	public void setMoasicOffset(float xOffset, float yOffset) {
		this.setMosaicOffsetX(xOffset);
		this.setMosaicOffsetY(yOffset);
	}
	
	// Update coords //
	private void updateTexCoordX() {
		
		this.textureWidth = this.width / this.tileWidth;
		this.textureX = (this.textureWidth / -2f) + this.mosaicOffsetX;
		this.updateTexCoords = true;
		
	}
	
	private void updateTexCoordY() {
		
		this.textureHeight = this.height / this.tileHeight;
		this.textureY = (this.textureHeight / -2f) + this.mosaicOffsetY;
		this.updateTexCoords = true;
		
	}
	
}
