package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.graphics.gui.render.GuiProgramMain;
import io.msengine.client.graphics.texture.DynTexture2D;
import io.msengine.client.graphics.texture.MapTexture2D;
import io.msengine.client.graphics.texture.ResTexture2D;
import io.msengine.client.graphics.texture.base.Texture;
import io.msengine.client.util.BufferAlloc;

public class GuiTextureExtended extends GuiObject {
	
	protected GuiBufferArray buf;
	
	protected int texName, texWidth, texHeight;
	protected float texCoordX, texCoordY, texCoordW, texCoordH;
	protected int borderLeft, borderTop, borderRight, borderBottom;
	protected int scale = 1;
	
	protected boolean updateBuffers;
	
	@Override
	protected void init() {
		this.buf = this.acquireProgram(GuiProgramMain.TYPE).createBuffer(false, true);
		if (this.isTextureValid()) {
			this.updateTextureBuffers();
		}
	}
	
	@Override
	protected void stop() {
		this.buf.close();
		this.buf = null;
	}
	
	@Override
	protected void render(float alpha) {
		
		if (!this.isTextureValid())
			return;
		
		if (this.updateBuffers) {
			this.updateTextureBuffers();
		}
		
		GuiProgramMain program = this.useProgram(GuiProgramMain.TYPE);
		
		this.model.push().translate(this.xIntOffset, this.yIntOffset).apply();
		program.setTextureUnitAndBind(0, this.texName);
		this.buf.draw();
		program.resetTextureUnitAndUnbind();
		this.model.pop();
		
	}
	
	@Override
	protected void update() { }
	
	@Override
	public float getAutoWidth() {
		return this.borderLeft + this.borderRight;
	}
	
	@Override
	public float getAutoHeight() {
		return this.borderTop + this.borderBottom;
	}
	
	protected void updateTextureBuffers() {
		
		//
		//   +---+
		//   | / |  <- Used Triangulation
		//   +---+
		//
		//   +0--+1------------2+9--+10
		//   |   |              |   |
		//   +3--+4------------5+11-+12
		//   |   |              |   |
		//   6   7            8 13  14
		//   +15-+16----------17+21-+22
		//   |   |              |   |
		//   +18-+19----------20+23-+24
		//
		// 25 vertices
		// 1 vertex = 2f(pos) + 2f(tex) = 4f
		// 25 vertices = 100f
		//
		// 18 triangles = 54 indices = 54i
		//
		
		this.buf.bindVao();
		
		BufferAlloc.allocStackFloat(100, buf -> {
			
			int width = (int) this.realWidth;
			int height = (int) this.realHeight;
			
			// Number of tex coord per pixel
			float widthRatio = this.texCoordW / this.texWidth;
			float heightRatio = this.texCoordH / this.texHeight;
			
			float texLeft = widthRatio * this.borderLeft;
			float texTop = heightRatio * this.borderTop;
			float texRight = widthRatio * this.borderRight;
			float texBottom = heightRatio * this.borderBottom;
			
			int left = this.borderLeft * this.scale;
			int top = this.borderTop * this.scale;
			int right = this.borderRight * this.scale;
			int bottom = this.borderBottom * this.scale;
			
			int x1 = +left;
			int x2 = width - right;
			int x3 = +width;
			int y1 = +top;
			int y2 = height - bottom;
			int y3 = +height;
			
			float tx0 = this.texCoordX;
			float tx1 = this.texCoordX + texLeft;
			float tx2 = this.texCoordX + (widthRatio * x2 / this.scale);
			float tx3 = this.texCoordX + this.texCoordW - texRight;
			float tx4 = this.texCoordX + this.texCoordW;
			float ty0 = this.texCoordY;
			float ty1 = this.texCoordY + texTop;
			float ty2 = this.texCoordY + (heightRatio * y2 / this.scale);
			float ty3 = this.texCoordY + this.texCoordH - texBottom;
			float ty4 = this.texCoordY + this.texCoordH;
			
			// Index: 0
			buf.put(0).put(0).put(tx0).put(ty0);
			buf.put(x1).put(0).put(tx1).put(ty0);
			buf.put(x2).put(0).put(tx2).put(ty0);
			buf.put(0).put(y1).put(tx0).put(ty1);
			buf.put(x1).put(y1).put(tx1).put(ty1);
			buf.put(x2).put(y1).put(tx2).put(ty1);
			buf.put(0).put(y2).put(tx0).put(ty2);
			buf.put(x1).put(y2).put(tx1).put(ty2);
			buf.put(x2).put(y2).put(tx2).put(ty2);
			// Index: 9
			buf.put(x2).put(0).put(tx3).put(ty0);
			buf.put(x3).put(0).put(tx4).put(ty0);
			buf.put(x2).put(y1).put(tx3).put(ty1);
			buf.put(x3).put(y1).put(tx4).put(ty1);
			buf.put(x2).put(y2).put(tx3).put(ty2);
			buf.put(x3).put(y2).put(tx4).put(ty2);
			// Index: 15
			buf.put(0).put(y2).put(tx0).put(ty3);
			buf.put(x1).put(y2).put(tx1).put(ty3);
			buf.put(x2).put(y2).put(tx2).put(ty3);
			buf.put(0).put(y3).put(tx0).put(ty4);
			buf.put(x1).put(y3).put(tx1).put(ty4);
			buf.put(x2).put(y3).put(tx2).put(ty4);
			// Index: 21
			buf.put(x2).put(y2).put(tx3).put(ty3);
			buf.put(x3).put(y2).put(tx4).put(ty3);
			buf.put(x2).put(y3).put(tx3).put(ty4);
			buf.put(x3).put(y3).put(tx4).put(ty4);
			
			buf.flip();
			this.buf.uploadVboData(0, buf, BufferUsage.DYNAMIC_DRAW);
			
		});
		
		BufferAlloc.allocStackInt(this.buf.setIndicesCount(54), buf -> {
			
			GuiCommon.putSquareIndices(buf, 0, 3, 4, 1);
			GuiCommon.putSquareIndices(buf, 1, 4, 5, 2);
			GuiCommon.putSquareIndices(buf, 9, 11, 12, 10);
			GuiCommon.putSquareIndices(buf, 3, 6, 7, 4);
			GuiCommon.putSquareIndices(buf, 4, 7, 8, 5);
			GuiCommon.putSquareIndices(buf, 11, 13, 14, 12);
			GuiCommon.putSquareIndices(buf, 15, 18, 19, 16);
			GuiCommon.putSquareIndices(buf, 16, 19, 20, 17);
			GuiCommon.putSquareIndices(buf, 21, 23, 24, 22);
			
			buf.flip();
			this.buf.uploadIboData(buf, BufferUsage.DYNAMIC_DRAW);
			
		});
		
		this.updateBuffers = false;
		
	}
	
	public boolean isTextureValid() {
		return this.texName > 0;
	}
	
	public void removeTexture() {
		this.texName = 0;
	}
	
	public void setTexture(int textureName, int textureWidth, int textureHeight) {
		this.texName = textureName;
		if (this.texWidth != textureWidth || this.texHeight != textureHeight) {
			this.texWidth = textureWidth;
			this.texHeight = textureHeight;
			this.updateBuffers = true;
		}
	}
	
	public void setTexture(Texture texture, int textureWidth, int textureHeight) {
		texture.checkValid();
		this.setTexture(texture.getName(), textureWidth, textureHeight);
	}
	
	public void setTexture(ResTexture2D texture) {
		this.setTexture(texture, texture.getWidth(), texture.getHeight());
	}
	
	public void setTexture(DynTexture2D texture) {
		this.setTexture(texture, texture.getWidth(), texture.getHeight());
	}
	
	public void setTextureCoords(float x, float y, float width, float height) {
		this.texCoordX = x;
		this.texCoordY = y;
		this.texCoordW = width;
		this.texCoordH = height;
		this.updateBuffers = true;
	}
	
	public void resetTextureCoords() {
		this.setTextureCoords(0, 0, 1, 1);
	}
	
	public void setTextureTile(MapTexture2D.Tile tile) {
		MapTexture2D map = tile.getMap();
		this.setTexture(map, (int) (map.getWidth() * tile.getWidth()), (int) (map.getHeight() * tile.getHeight()));
		this.setTextureCoords(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
	}
	
	public void setBorders(int left, int top, int right, int bottom) {
		this.borderLeft = left;
		this.borderTop = top;
		this.borderRight = right;
		this.borderBottom = bottom;
		this.updateBuffers = true;
		this.updateOffsets();
	}
	
	public int getScale() {
		return this.scale;
	}
	
	public void setScale(int scale) {
		if (scale < 1) {
			throw new IllegalArgumentException("Invalid scale, must be a non-null positive integer.");
		} else if (this.scale != scale) {
			this.scale = scale;
			this.updateBuffers = true;
		}
	}
	
}
