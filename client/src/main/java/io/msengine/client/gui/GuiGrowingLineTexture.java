package io.msengine.client.gui;

import io.msengine.client.renderer.texture.TextureMapTile;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiGrowingLineTexture extends GuiComponentTexture {
	
	private final Map<String, TextureMapTile> statesTiles;
	
	private float defaultHeight;
	private float bordersWidth;
	private float scale;
	
	public GuiGrowingLineTexture() {
		
		this.statesTiles = new HashMap<>();
		this.setBordersWidth(2f);
		
	}
	
	@Override
	protected boolean isStateReady() {
		return this.statesTiles.containsKey(this.state);
	}
	
	@Override
	protected IntBuffer initBuffers(AtomicInteger verticesCount, AtomicInteger texCoordsCount) {
		
		IntBuffer buf = MemoryUtil.memAllocInt(this.buffer.setIndicesCount(12));
		buf.put(0).put(1).put(3);
		buf.put(1).put(2).put(3);
		buf.put(4).put(5).put(7);
		buf.put(5).put(6).put(7);
		verticesCount.set(16);
		texCoordsCount.set(16);
		
		return buf;
		
	}
	
	@Override
	protected FloatBuffer updateVerticesBuffer() {
		
		float growingWidth = this.width - (this.bordersWidth * this.scale);
		
		FloatBuffer buf = MemoryUtil.memAllocFloat(16);
		buf.put(0).put(0);
		buf.put(0).put(this.height);
		buf.put(growingWidth).put(this.height);
		buf.put(growingWidth).put(0);
		buf.put(growingWidth).put(0);
		buf.put(growingWidth).put(this.height);
		buf.put(this.width).put(this.height);
		buf.put(this.width).put(0);
		
		return buf;
		
	}
	
	@Override
	protected FloatBuffer updateTexCoordsBuffer() {
		
		TextureMapTile tile = this.statesTiles.get(this.state);
		
		float textureWidth = (float) this.texture.getWidth();
		float nonScaledGrowingWidth = (this.width / this.scale) - this.bordersWidth;
		
		if (nonScaledGrowingWidth < 0)
			nonScaledGrowingWidth = 0;
		
		float fixTileWidth = this.bordersWidth / textureWidth;
		float growingTileWidth = nonScaledGrowingWidth / textureWidth;
		
		FloatBuffer buf = MemoryUtil.memAllocFloat(16);
		
		buf.put(tile.x).put(tile.y);
		buf.put(tile.x).put(tile.y + tile.height);
		buf.put(tile.x + growingTileWidth).put(tile.y + tile.height);
		buf.put(tile.x + growingTileWidth).put(tile.y);
		
		buf.put(tile.x + tile.width - fixTileWidth).put(tile.y);
		buf.put(tile.x + tile.width - fixTileWidth).put(tile.y + tile.height);
		buf.put(tile.x + tile.width).put(tile.y + tile.height);
		buf.put(tile.x + tile.width).put(tile.y);
		
		return buf;
		
	}
	
	@Override
	public float getAutoWidth() {
		return this.bordersWidth * 2;
	}
	
	@Override
	public float getAutoHeight() {
		return this.defaultHeight;
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.updateVertices = true;
		this.updateTexCoords = true;
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.updateScale();
	}
	
	@Override
	public void setState(String state) {
		super.setState(state);
		this.updateTexCoords = true;
	}
	
	public void setStateTile(String state, TextureMapTile tile) {
		
		Objects.requireNonNull(tile);
		
		if (this.texture == null) {
			this.texture = tile.map.getTextureObject();
		} else if (this.texture != tile.map.getTextureObject()) {
			throw new IllegalArgumentException("The given map tile is not mapped to the same texture object.");
		}
		
		this.statesTiles.put(state, tile);
		this.updateTexCoords = true;
		
	}
	
	public float getDefaultHeight() {
		return this.defaultHeight;
	}
	
	public void setDefaultHeight(float defaultHeight) {
		this.defaultHeight = defaultHeight;
		this.updateScale();
	}
	
	public float getBordersWidth() {
		return this.bordersWidth;
	}
	
	public void setBordersWidth(float bordersWidth) {
		this.bordersWidth = bordersWidth;
		this.updateVertices = true;
		this.updateTexCoords = true;
	}
	
	private void updateScale() {
		this.scale = this.height / this.defaultHeight;
		this.updateVertices = true;
		this.updateTexCoords = true;
	}
	
}
