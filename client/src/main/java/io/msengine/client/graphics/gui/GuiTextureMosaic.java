package io.msengine.client.graphics.gui;

public class GuiTextureMosaic extends GuiTexture {
	
	protected float tileWidth = 1f;
	protected float tileHeight = 1f;
	
	protected float mosaicOffsetX = 0f;
	protected float mosaicOffsetY = 0f;
	
	@Override
	protected void onWidthChanged(float width) {
		super.onWidthChanged(width);
		this.updateTexCoordX();
	}
	
	@Override
	protected void onHeightChanged(float height) {
		super.onHeightChanged(height);
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
		
		this.textureWidth = this.width / this.tileWidth;
		this.textureX = (this.textureWidth / -2f) + this.mosaicOffsetX;
		this.updateTexCoords = true;
		
	}
	
	private void updateTexCoordY() {
		
		this.textureHeight = this.height / this.tileHeight;
		this.textureY = (this.textureHeight / -2f) + this.mosaicOffsetY;
		this.updateTexCoords = true;
		
	}
	
	@Override
	protected void buildToString(StringBuilder builder) {
		super.buildToString(builder);
		builder.append(", tileSize=").append(this.tileWidth).append('/').append(this.tileHeight);
		builder.append(", mosaicOffset=").append(this.mosaicOffsetX).append('/').append(this.mosaicOffsetY);
	}
	
}
