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
	
	protected float mosaicXOffset;
	protected float mosaicYOffset;
	
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
	public float getMosaicXOffset() {
		return mosaicXOffset;
	}
	
	public void setMosaicXOffset(float mosaicXOffset) {
		this.mosaicXOffset = mosaicXOffset;
		this.updateTexCoordX();
	}
	
	public float getMosaicYOffset() {
		return mosaicYOffset;
	}
	
	public void setMosaicYOffset(float mosaicYOffset) {
		this.mosaicYOffset = mosaicYOffset;
		this.updateTexCoordY();
	}
	
	public void setMoasicOffset(float xOffset, float yOffset) {
		this.setMosaicXOffset(xOffset);
		this.setMosaicYOffset(yOffset);
	}
	
	// Update coords //
	private void updateTexCoordX() {
		
		this.textureWidth = this.width / this.tileWidth;
		this.textureX = (this.textureWidth / -2f) + this.xOffset;
		this.updateTexCoords = true;
		
	}
	
	private void updateTexCoordY() {
		
		this.textureHeight = this.height / this.tileHeight;
		this.textureY = (this.textureHeight / -2f) + this.yOffset;
		this.updateTexCoords = true;
		
	}
	
}
