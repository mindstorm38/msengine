package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.util.BufferAlloc;

public class GuiTexture extends GuiObject {
	
	protected GuiBufferArray buf;
	protected boolean updateVertices;
	protected boolean updateTexCoords;
	
	protected int textureName;
	protected int textureX;
	protected int textureY;
	protected int textureWidth;
	protected int textureHeight;
	
	@Override
	protected void init() {
		this.buf = this.getProgram().createBufferSep(false, true);
		this.initBuffers();
	}
	
	@Override
	protected void stop() {
		this.buf.close();
		this.buf = null;
	}
	
	@Override
	protected void render(float alpha) {
		
		if (this.textureName == 0)
			return;
		
		if (this.updateVertices) {
			this.updateVerticesBuffer();
		}
		
		if (this.updateTexCoords) {
			this.updateTexCoordsBuffer();
		}
		
		this.model.push().translate(this.xIntOffset, this.yIntOffset).apply();
		this.manager.bindTexture(this.textureName);
		this.manager.setTextureEnabled(true);
		this.buf.draw();
		this.manager.setTextureEnabled(false);
		this.model.pop();
		
	}
	
	@Override
	protected void update() { }
	
	private void initBuffers() {
		
		this.buf.bindVao();
		this.buf.allocateVboData(this.buf.getPositionIndex(), 8 << 2, BufferUsage.DYNAMIC_DRAW);
		this.buf.allocateVboData(this.buf.getTexCoordsBufIdx(), 8 << 2, BufferUsage.DYNAMIC_DRAW);
		
		BufferAlloc.allocStackInt(this.buf.setIndicesCount(6), buf -> {
			GuiCommon.putSquareIndices(buf);
			buf.flip();
			this.buf.uploadIboData(buf, BufferUsage.STATIC_DRAW);
		});
		
		this.updateVerticesBuffer();
		this.updateTexCoordsBuffer();
		
	}
	
	private void updateVerticesBuffer() {
		
		BufferAlloc.allocStackFloat(8, buf -> {
			GuiCommon.putSquareVertices(buf, this.width, this.height);
			buf.flip();
			this.buf.bindVao();
			this.buf.uploadVboSubData(this.buf.getPositionIndex(), 0, buf);
		});
		
		this.updateVertices = false;
		
	}
	
	private void updateTexCoordsBuffer() {
		
		BufferAlloc.allocStackFloat(8, buf -> {
			
			float x = this.textureX;
			float y = this.textureY;
			float w = this.textureWidth;
			float h = this.textureHeight;
			
			buf.put(x).put(y);
			buf.put(x).put(y + h);
			buf.put(x + w).put(y + h);
			buf.put(x + w).put(y);
			buf.flip();
			this.buf.bindVao();
			this.buf.uploadVboSubData(this.buf.getTexCoordsBufIdx(), 0, buf);
			
		});
		
		this.updateTexCoords = false;
		
	}
	
}
