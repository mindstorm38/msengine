package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.graphics.util.BufferAlloc;
import io.msengine.client.graphics.util.BufferUsage;
import io.msengine.common.util.Color;

import java.nio.FloatBuffer;

/**
 * Base class for color squares.
 * @author Theo Rozier
 */
public abstract class GuiColorBase extends GuiObject {

	protected GuiBufferArray buf;
	protected boolean updateVertices;
	protected boolean updateColors;
	
	@Override
	protected void init() {
		this.buf = this.getProgram().createBufferSep(true, false);
		this.initBuffers();
	}
	
	@Override
	protected void stop() {
		this.buf.close();
		this.buf = null;
	}
	
	@Override
	protected void render(float alpha) {
		
		if (this.updateVertices) {
			this.updateVerticesBuffer();
		}
		
		if (this.updateColors) {
			this.updateColorsBuffer();
		}
		
		this.model.push().translate(this.xOffset, this.yOffset).apply();
		this.buf.draw();
		this.model.pop();
		
	}
	
	@Override
	protected void update() { }
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.updateVertices = true;
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.updateVertices = true;
	}
	
	private void initBuffers() {
		
		this.buf.bindVao();
		this.buf.allocateVboData(this.buf.getPositionIndex(), 8 << 2, BufferUsage.DYNAMIC_DRAW);
		this.buf.allocateVboData(this.buf.getColorIndex(), 16 << 2, BufferUsage.DYNAMIC_DRAW);
		
		BufferAlloc.allocStackInt(this.buf.setIndicesCount(6), buf -> {
			buf.put(0).put(1).put(3);
			buf.put(1).put(2).put(3);
			buf.flip();
			this.buf.uploadIboData(buf, BufferUsage.STATIC_DRAW);
		});
		
		this.updateVerticesBuffer();
		this.updateColorsBuffer();
		
	}
	
	private void updateVerticesBuffer() {
		
		BufferAlloc.allocStackFloat(8, buf -> {
			buf.put(0).put(0);
			buf.put(0).put(this.height);
			buf.put(this.width).put(this.height);
			buf.put(this.width).put(0);
			buf.flip();
			this.buf.bindVao();
			this.buf.uploadVboSubData(this.buf.getPositionIndex(), 0, buf);
		});
		
		this.updateVertices = false;
		
	}
	
	private void updateColorsBuffer() {
		
		BufferAlloc.allocStackFloat(16, buf -> {
			putCornerColorToBuffer(buf, 0);
			putCornerColorToBuffer(buf, 3);
			putCornerColorToBuffer(buf, 2);
			putCornerColorToBuffer(buf, 1);
			buf.flip();
			this.buf.bindVao();
			this.buf.uploadVboSubData(this.buf.getColorIndex(), 0, buf);
		});
		
		this.updateColors = false;
		
	}
	
	/**
	 * Tel this color square to update.
	 */
	protected void updateColors() {
		this.updateColors = true;
	}
	
	/**
	 * Internal method to get corner color and put it in a float buffer.
	 * @param buffer The float buffer containing colors.
	 * @param corner The corner index.
	 */
	private void putCornerColorToBuffer(FloatBuffer buffer, int corner) {
		Color color = this.getCornerColor(corner);
		(color == null ? Color.WHITE : color).putToBuffer(buffer, true);
	}
	
	/**
	 * Method to implement used to update colors buffer.<br>
	 * Corner indices :
	 * <ul>
	 *     <li>0: Top Left</li>
	 *     <li>1: Top Right</li>
	 *     <li>2: Bottom Right</li>
	 *     <li>3: Bottom Left</li>
	 * </ul>
	 * @param corner Corner index.
	 * @return The color to set at this corner, if null returned, {@link Color#WHITE} is used.
	 */
	public abstract Color getCornerColor(int corner);
	
}
