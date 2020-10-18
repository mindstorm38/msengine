package io.msengine.client.graphics.gui.mask;

import io.msengine.client.graphics.util.BufferUsage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.graphics.util.BufferType;

public class GuiMaskRectangle extends GuiMask {
	
	private float x, y;
	private float width, height;
	
	private GuiBufferArray buf;
	private boolean updateVertices;
	
	public GuiMaskRectangle(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public GuiMaskRectangle() {
		this(0, 0, 1, 1);
	}
	
	@Override
	public void init() {
		this.buf = this.getProgram().createBuffer(false, false);
		allocBuffers();
	}
	
	@Override
	public void stop() {
		this.buf.close();
		this.buf = null;
	}
	
	@Override
	public void draw() {
		
		if (this.updateVertices) {
			this.updateVerticesBuffer();
		}
		
		this.model.translate(this.x, this.y).apply();
		this.buf.draw();
		this.model.pop();
		
	}
	
	private void allocBuffers() {
		
		this.buf.bindVao();
		this.buf.allocateVboData(0, 8 << 2, BufferUsage.DYNAMIC_DRAW);
		
		BufferType.INT.alloc(this.buf.setIndicesCount(6), buf -> {
			buf.put(0).put(1).put(3);
			buf.put(1).put(2).put(3);
			buf.flip();
			this.buf.uploadIboData(buf, BufferUsage.STATIC_DRAW);
		});
		
	}
	
	private void updateVerticesBuffer() {
		
		this.buf.bindVao();
		
		BufferType.FLOAT.alloc(8, buf -> {
			buf.put(0).put(0);
			buf.put(0).put(this.height);
			buf.put(this.width).put(this.height);
			buf.put(this.width).put(0);
			this.buf.uploadVboSubData(0, 0, buf);
		});
		
		this.updateVertices = false;
		
	}
	
	// Pos //
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setPosition(float x, float y) {
		this.setX(x);
		this.setY(y);
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	// Size //
	
	public void setWidth(float width) {
		if (this.width != width) {
			this.width = width;
			this.updateVertices = true;
		}
	}
	
	public void setHeight(float height) {
		if (this.height != height) {
			this.height = height;
			this.updateVertices = true;
		}
	}
	
	public void setSize(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
	}
	
	// Both pos and size //
	
	public void setPositionSize(float x, float y, float width, float height) {
		this.setPosition(x, y);
		this.setSize(width, height);
	}
	
}
