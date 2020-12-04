package io.msengine.client.gui;

import io.msengine.client.renderer.texture.TextureObject;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static io.msengine.client.renderer.vertex.type.GuiFormat.*;

@Deprecated
public abstract class GuiComponentTexture extends GuiObject {
	
	protected IndicesDrawBuffer buffer;
	protected boolean updateVertices;
	protected boolean updateTexCoords;
	
	protected TextureObject texture;
	protected String state;
	
	public GuiComponentTexture() {
	
	}
	
	@Override
	protected void init() {
		
		this.buffer = this.renderer.createDrawBuffer( false, true );
		this._initBuffers();
		
	}
	
	@Override
	protected void stop() {
	
		this.buffer.delete();
		this.buffer = null;
		
	}
	
	protected abstract boolean isStateReady();
	protected abstract IntBuffer initBuffers(AtomicInteger verticesCount, AtomicInteger texCoordsCount);
	protected abstract FloatBuffer updateVerticesBuffer();
	protected abstract FloatBuffer updateTexCoordsBuffer();
	
	protected void _initBuffers() {
		
		IntBuffer indicesBuffer = null;
		
		try {
			
			AtomicInteger verticesCount = new AtomicInteger();
			AtomicInteger texCoordsCount = new AtomicInteger();
			
			indicesBuffer = this.initBuffers(verticesCount, texCoordsCount);
			indicesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.allocateVboData(GUI_POSITION, verticesCount.get() << 2, BufferUsage.DYNAMIC_DRAW);
			this.buffer.allocateVboData(GUI_TEX_COORD, texCoordsCount.get() << 2, BufferUsage.DYNAMIC_DRAW);
			this.buffer.uploadIboData(indicesBuffer, BufferUsage.STATIC_DRAW);
			
		} finally {
			BufferUtils.safeFree(indicesBuffer);
		}
		
		this._updateVerticesBuffer();
		this._updateTexCoordsBuffer();
		
	}
	
	protected void _updateVerticesBuffer() {
		
		if (this.isStateReady()) {
			
			FloatBuffer verticesBuffer = null;
			
			try {
				
				verticesBuffer = this.updateVerticesBuffer();
				verticesBuffer.flip();
				
				this.buffer.bindVao();
				this.buffer.uploadVboSubData(GUI_POSITION, 0, verticesBuffer);
				
			} finally {
				BufferUtils.safeFree(verticesBuffer);
			}
			
			this.updateVertices = false;
			
		}
		
	}
	
	protected void _updateTexCoordsBuffer() {
		
		if (this.isStateReady()) {
			
			FloatBuffer texCoordsBuffer = null;
			
			try {
				
				texCoordsBuffer = this.updateTexCoordsBuffer();
				texCoordsBuffer.flip();
				
				this.buffer.bindVao();
				this.buffer.uploadVboSubData(GUI_TEX_COORD, 0, texCoordsBuffer);
				
			} finally {
				BufferUtils.safeFree(texCoordsBuffer);
			}
			
			this.updateTexCoords = false;
			
		}
		
	}
	
	@Override
	public void render(float alpha) {
	
		if (!this.isStateReady())
			return;
		
		if (this.updateVertices)
			this._updateVerticesBuffer();
		
		if (this.updateTexCoords)
			this._updateTexCoordsBuffer();
		
		this.model.push().translate(this.xIntOffset, this.yIntOffset).apply();
			this.renderer.setTextureSampler(this.texture);
				this.buffer.drawElements();
			this.renderer.resetTextureSampler();
		this.model.pop();
		
	}
	
	@Override
	public void update() {
	
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
}
