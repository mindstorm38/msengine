package io.msengine.client.gui;

import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.common.util.Color;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static io.msengine.client.renderer.vertex.type.GuiFormat.GUI_POSITION;
import static io.msengine.client.renderer.vertex.type.GuiFormat.GUI_TEX_COORD;

/**
 *
 * Base class for color squares.
 *
 * @author Theo Rozier
 *
 */
public abstract class GuiColorBase extends GuiObject {
	
	protected IndicesDrawBuffer buffer;
	protected boolean updateVertices;
	protected boolean updateColors;
	
	@Override
	protected void init() {
	
		this.buffer = this.renderer.createDrawBuffer(true, false);
		this.initBuffers();
		
	}
	
	@Override
	protected void stop() {
	
		this.buffer.delete();
		this.buffer = null;
		
	}
	
	private void initBuffers() {
		
		IntBuffer indicesBuffer = null;
		
		try {
			
			indicesBuffer = MemoryUtil.memAllocInt(this.buffer.setIndicesCount(6));
			
			indicesBuffer.put(0).put(1).put(3);
			indicesBuffer.put(1).put(2).put(3);
			
			indicesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.allocateVboData(GUI_POSITION, 8 << 2, BufferUsage.DYNAMIC_DRAW);
			this.buffer.allocateVboData(GUI_TEX_COORD, 16 << 2, BufferUsage.DYNAMIC_DRAW);
			this.buffer.uploadIboData(indicesBuffer, BufferUsage.STATIC_DRAW);
			
		} finally {
			BufferUtils.safeFree(indicesBuffer);
		}
		
		this.updateVerticesBuffer();
		this.updateColorsBuffer();
		
	}
	
	private void updateVerticesBuffer() {
		
		FloatBuffer verticesBuffer = null;
		
		try {
			
			verticesBuffer = MemoryUtil.memAllocFloat(8);
			
			verticesBuffer.put(0).put(0);
			verticesBuffer.put(0).put( this.height);
			verticesBuffer.put(this.width).put(this.height);
			verticesBuffer.put(this.width).put(0);
			
			verticesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.uploadVboSubData(GUI_POSITION, 0, verticesBuffer);
			
		} finally {
			BufferUtils.safeFree(verticesBuffer);
		}
		
		this.updateVertices = false;
		
	}
	
	private void updateColorsBuffer() {
		
		FloatBuffer colorsBuffer = null;
		
		try {
			
			colorsBuffer = MemoryUtil.memAllocFloat(16);
			
			putCornerColorToBuffer(colorsBuffer, 0);
			putCornerColorToBuffer(colorsBuffer, 3);
			putCornerColorToBuffer(colorsBuffer, 2);
			putCornerColorToBuffer(colorsBuffer, 1);
			
			colorsBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.uploadVboSubData(GUI_TEX_COORD, 0, colorsBuffer);
			
		} finally {
			BufferUtils.safeFree(colorsBuffer);
		}
		
		this.updateColors = false;
		
	}
	
	@Override
	public void render(float alpha) {
	
		if (this.updateVertices)
			this.updateVerticesBuffer();
		
		if (this.updateColors)
			this.updateColorsBuffer();
		
		this.model.push().translate(this.xOffset, this.yOffset).apply();
		this.buffer.drawElements();
		this.model.pop();
		
	}
	
	@Override
	public void update() {}
	
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
	
	/**
	 * Tel this color square to update.
	 */
	public void updateColors() {
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
