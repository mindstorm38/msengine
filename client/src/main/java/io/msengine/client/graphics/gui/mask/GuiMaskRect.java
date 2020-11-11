package io.msengine.client.graphics.gui.mask;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.buffer.IndexedBufferArray;
import io.msengine.client.graphics.gui.GuiCommon;
import io.msengine.client.graphics.gui.render.GuiProgramMask;
import io.msengine.client.util.BufferAlloc;

public class GuiMaskRect extends GuiMask {
	
	private IndexedBufferArray buf;
	
	@Override
	protected void initMask() {
		this.buf = this.manager.acquireProgram(GuiProgramMask.TYPE).createBuffer();
		this.initBuffer();
	}
	
	@Override
	protected void stopMask() {
		this.manager.releaseProgram(GuiProgramMask.TYPE);
		this.buf.close();
		this.buf = null;
	}
	
	@Override
	public void draw() {
		this.useProgram(GuiProgramMask.TYPE);
		this.model.push().translate(this.xOffset, this.yOffset).scale(this.realWidth, this.realHeight).apply();
		this.buf.draw();
		this.model.pop();
	}
	
	public void initBuffer() {
		
		this.buf.bindVao();
		
		BufferAlloc.allocStackFloat(8, buf -> {
			GuiCommon.putSquareVertices(buf, 1, 1);
			buf.flip();
			this.buf.uploadVboData(0, buf, BufferUsage.STATIC_DRAW);
		});
		
		BufferAlloc.allocStackInt(this.buf.setIndicesCount(6), buf -> {
			GuiCommon.putSquareIndices(buf);
			buf.flip();
			this.buf.uploadIboData(buf, BufferUsage.STATIC_DRAW);
		});
		
	}
	
}
