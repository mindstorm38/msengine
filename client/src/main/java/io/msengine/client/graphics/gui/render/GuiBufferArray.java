package io.msengine.client.graphics.gui.render;

import io.msengine.client.graphics.buffer.IndexedBufferArray;

public class GuiBufferArray extends IndexedBufferArray {
	
	private final int colorBufIdx;
	private final int texCoordBufIdx;
	
	public GuiBufferArray(int vao, int[] vbos, int colorBufIdx, int texCoordBufIdx) {
		super(vao, vbos);
		this.colorBufIdx = colorBufIdx;
		this.texCoordBufIdx = texCoordBufIdx;
	}
	
	public GuiBufferArray(int vao, int[] vbos) {
		this(vao, vbos, 0, 0);
	}
	
	public int getPositionIndex() {
		return 0;
	}
	
	public int getColorIndex() {
		return this.colorBufIdx;
	}
	
	public int getTexCoordBufIdx() {
		return this.texCoordBufIdx;
	}
	
}
