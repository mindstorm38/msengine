package io.msengine.client.graphics.gui;

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
		
		this.textureWidth = this.getRealWidth() / this.tileWidth;
		this.textureX = (this.textureWidth / -2f) + this.mosaicOffsetX;
		this.updateTexCoord = true;
		
	}
	
	private void updateTexCoordY() {
		
		this.textureHeight = this.getRealHeight() / this.tileHeight;
		this.textureY = (this.textureHeight / -2f) + this.mosaicOffsetY;
		this.updateTexCoord = true;
		
	}
	
	@Override
	protected void buildToString(StringBuilder builder) {
		super.buildToString(builder);
		builder.append(", tileSize=").append(this.tileWidth).append('/').append(this.tileHeight);
		builder.append(", mosaicOffset=").append(this.mosaicOffsetX).append('/').append(this.mosaicOffsetY);
	}
	
}
