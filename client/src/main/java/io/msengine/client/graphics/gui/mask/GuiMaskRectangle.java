package io.msengine.client.graphics.gui.mask;

import io.msengine.client.graphics.gui.GuiCommon;
import io.msengine.client.graphics.gui.render.GuiProgramMain;
import io.msengine.client.graphics.gui.render.GuiStdProgram;
import io.msengine.client.util.BufferAlloc;
import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;

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
		//this.buf = this.getProgram().createBuffer(false, false);
		this.buf = this.manager.acquireProgram(GuiProgramMain.TYPE).createBuffer(false, false);
		initBuffers();
	}
	
	@Override
	public void stop() {
		this.manager.releaseProgram(GuiProgramMain.TYPE);
		this.buf.close();
		this.buf = null;
	}
	
	@Override
	public void draw() {
		
		if (this.updateVertices) {
			this.updateVerticesBuffer();
		}
		
		this.manager.useProgram(GuiProgramMain.TYPE);
		this.model.translate(this.x, this.y).apply();
		this.buf.draw();
		this.model.pop();
		
	}
	
	private void initBuffers() {
		
		this.buf.bindVao();
		this.buf.allocateVboData(0, 8 << 2, BufferUsage.DYNAMIC_DRAW);
		
		BufferAlloc.allocStackInt(this.buf.setIndicesCount(6), buf -> {
			GuiCommon.putSquareIndices(buf);
			buf.flip();
			this.buf.uploadIboData(buf, BufferUsage.STATIC_DRAW);
		});
		
		this.updateVerticesBuffer();
		
	}
	
	private void updateVerticesBuffer() {
		
		BufferAlloc.allocStackFloat(8, buf -> {
			GuiCommon.putSquareVertices(buf, this.width, this.height);
			buf.flip();
			this.buf.bindVao();
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
