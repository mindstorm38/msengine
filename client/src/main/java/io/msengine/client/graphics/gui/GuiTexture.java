package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.graphics.gui.render.GuiProgramMain;
import io.msengine.client.graphics.texture.base.Texture;
import io.msengine.client.util.BufferAlloc;

public class GuiTexture extends GuiObject {
	
	protected GuiBufferArray buf;
	protected boolean updateVertices;
	protected boolean updateTexCoords;
	
	protected int textureName;
	protected float textureX;
	protected float textureY;
	protected float textureWidth;
	protected float textureHeight;
	
	@Override
	protected void init() {
		this.buf = this.acquireProgram(GuiProgramMain.TYPE).createBufferSep(false, true);
		this.initBuffers();
	}
	
	@Override
	protected void stop() {
		this.releaseProgram(GuiProgramMain.TYPE);
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
		
		GuiProgramMain program = this.useProgram(GuiProgramMain.TYPE);
		
		this.model.push().translate(this.xIntOffset, this.yIntOffset).apply();
		program.setTextureUnitAndBind(0, this.textureName);
		this.buf.draw();
		program.resetTextureUnitAndUnbind();
		this.model.pop();
		
	}
	
	@Override
	protected void update() { }
	
	@Override
	public void onRealWidthChanged() {
		super.onRealWidthChanged();
		this.updateVertices = true;
	}
	
	@Override
	public void onRealHeightChanged() {
		super.onRealHeightChanged();
		this.updateVertices = true;
	}
	
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
			GuiCommon.putSquareVertices(buf, this.getRealWidth(), this.getRealHeight());
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
	
	public void setTexture(int name) {
		this.textureName = name;
	}
	
	public void setTexture(Texture texture) {
		texture.checkValid();
		this.setTexture(texture.getName());
	}
	
	public void setTextureCoords(float x, float y, float width, float height) {
		this.textureX = x;
		this.textureY = y;
		this.textureWidth = width;
		this.textureHeight = height;
		this.updateTexCoords = true;
	}
	
	public void resetTextureCoords() {
		this.setTextureCoords(0, 0, 1, 1);
	}
	
	public void setTextureFull(int name) {
		this.setTexture(name);
		this.resetTextureCoords();
	}
	
	public void setTextureFull(Texture texture) {
		this.setTexture(texture);
		this.resetTextureCoords();
	}
	
	@Override
	protected void buildToString(StringBuilder builder) {
		super.buildToString(builder);
		builder.append(", tex=").append(this.textureName);
		builder.append(", texPos=").append(this.textureX).append('/').append(this.textureY);
		builder.append(", texSize=").append(this.textureWidth).append('/').append(this.textureHeight);
	}
	
}
