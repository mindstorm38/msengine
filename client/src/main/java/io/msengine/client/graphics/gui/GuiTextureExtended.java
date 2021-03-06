package io.msengine.client.graphics.gui;

import io.msengine.client.graphics.buffer.BufferUsage;
import io.msengine.client.graphics.gui.render.GuiBufferArray;
import io.msengine.client.graphics.gui.render.GuiProgramMain;
import io.msengine.client.graphics.texture.DynTexture2D;
import io.msengine.client.graphics.texture.MapTexture2D;
import io.msengine.client.graphics.texture.ResTexture2D;
import io.msengine.client.graphics.texture.base.Texture;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class GuiTextureExtended extends GuiObject {
	
	public static final String DEFAULT_STATE = "default";
	
	protected GuiBufferArray buf;
	protected boolean updateBuffers;
	protected int scale = 1;
	
	protected final Map<String, StateTracker> states = new HashMap<>();
	protected StateTracker currentState;
	
	@Override
	protected void init() {
		this.buf = this.acquireProgram(GuiProgramMain.TYPE).createBuffer(false, true);
		this.updateTextureBuffers();
	}
	
	@Override
	protected void stop() {
		this.buf.close();
		this.buf = null;
	}
	
	@Override
	protected void render(float alpha) {
		
		if (this.updateBuffers) {
			this.updateTextureBuffers();
		}
		
		StateTracker tracker = this.currentState;
		
		if (tracker == null || !tracker.state.isTextureValid())
			return;
		
		GuiProgramMain program = this.useProgram(GuiProgramMain.TYPE);
		
		this.model.push().translate(this.xIntOffset, this.yIntOffset).apply();
		program.setTextureUnitAndBind(0, tracker.state.texName);
		this.buf.draw(tracker.bufferOffset, tracker.bufferLength);
		program.resetTextureUnitAndUnbind();
		this.model.pop();
		
	}
	
	@Override
	protected void update() { }
	
	@Override
	public float getAutoWidth() {
		return this.currentState == null ? 0 : this.currentState.getAutoWidth() * this.scale;
	}
	
	@Override
	public float getAutoHeight() {
		return this.currentState == null ? 0 : this.currentState.getAutoHeight() * this.scale;
	}
	
	@Override
	public void onRealWidthChanged() {
		super.onRealWidthChanged();
		this.updateBuffers = true;
	}
	
	@Override
	public void onRealHeightChanged() {
		super.onRealHeightChanged();
		this.updateBuffers = true;
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
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			int statesCount = this.states.size();
			
			FloatBuffer data = stack.mallocFloat(statesCount * 100);
			IntBuffer indices = stack.mallocInt(statesCount * 54);
			
			int width = (int) this.realWidth;
			int height = (int) this.realHeight;
			int idx = 0;
			
			for (StateTracker tracker : this.states.values()) {
				
				State state = tracker.state;
				
				// Number of tex coord per pixel
				float widthRatio = state.texCoordW / state.texWidth;
				float heightRatio = state.texCoordH / state.texHeight;
				
				float texLeft = widthRatio * state.borderLeft;
				float texTop = heightRatio * state.borderTop;
				float texRight = widthRatio * state.borderRight;
				float texBottom = heightRatio * state.borderBottom;
				
				int left = state.borderLeft * this.scale;
				int top = state.borderTop * this.scale;
				int right = state.borderRight * this.scale;
				int bottom = state.borderBottom * this.scale;
				
				int x1 = +left;
				int x2 = width - right;
				int x3 = +width;
				int y1 = +top;
				int y2 = height - bottom;
				int y3 = +height;
				
				float tx0 = state.texCoordX;
				float tx1 = state.texCoordX + texLeft;
				float tx2 = state.texCoordX + (widthRatio * x2 / this.scale);
				float tx3 = state.texCoordX + state.texCoordW - texRight;
				float tx4 = state.texCoordX + state.texCoordW;
				float ty0 = state.texCoordY;
				float ty1 = state.texCoordY + texTop;
				float ty2 = state.texCoordY + (heightRatio * y2 / this.scale);
				float ty3 = state.texCoordY + state.texCoordH - texBottom;
				float ty4 = state.texCoordY + state.texCoordH;
				
				// Index: 0
				data.put(0).put(0).put(tx0).put(ty0);
				data.put(x1).put(0).put(tx1).put(ty0);
				data.put(x2).put(0).put(tx2).put(ty0);
				data.put(0).put(y1).put(tx0).put(ty1);
				data.put(x1).put(y1).put(tx1).put(ty1);
				data.put(x2).put(y1).put(tx2).put(ty1);
				data.put(0).put(y2).put(tx0).put(ty2);
				data.put(x1).put(y2).put(tx1).put(ty2);
				data.put(x2).put(y2).put(tx2).put(ty2);
				// Index: 9
				data.put(x2).put(0).put(tx3).put(ty0);
				data.put(x3).put(0).put(tx4).put(ty0);
				data.put(x2).put(y1).put(tx3).put(ty1);
				data.put(x3).put(y1).put(tx4).put(ty1);
				data.put(x2).put(y2).put(tx3).put(ty2);
				data.put(x3).put(y2).put(tx4).put(ty2);
				// Index: 15
				data.put(0).put(y2).put(tx0).put(ty3);
				data.put(x1).put(y2).put(tx1).put(ty3);
				data.put(x2).put(y2).put(tx2).put(ty3);
				data.put(0).put(y3).put(tx0).put(ty4);
				data.put(x1).put(y3).put(tx1).put(ty4);
				data.put(x2).put(y3).put(tx2).put(ty4);
				// Index: 21
				data.put(x2).put(y2).put(tx3).put(ty3);
				data.put(x3).put(y2).put(tx4).put(ty3);
				data.put(x2).put(y3).put(tx3).put(ty4);
				data.put(x3).put(y3).put(tx4).put(ty4);
				
				int i = idx * 25;
				tracker.bufferOffset = idx * 54;
				tracker.bufferLength = 54;
				idx++;
				
				GuiCommon.putSquareIndices(indices, i, i + 3, i + 4, i + 1);
				GuiCommon.putSquareIndices(indices, i + 1, i + 4, i + 5, i + 2);
				GuiCommon.putSquareIndices(indices, i + 9, i + 11, i + 12, i + 10);
				GuiCommon.putSquareIndices(indices, i + 3, i + 6, i + 7, i + 4);
				GuiCommon.putSquareIndices(indices, i + 4, i + 7, i + 8, i + 5);
				GuiCommon.putSquareIndices(indices, i + 11, i + 13, i + 14, i + 12);
				GuiCommon.putSquareIndices(indices, i + 15, i + 18, i + 19, i + 16);
				GuiCommon.putSquareIndices(indices, i + 16, i + 19, i + 20, i + 17);
				GuiCommon.putSquareIndices(indices, i + 21, i + 23, i + 24, i + 22);
				
			}
			
			data.flip();
			indices.flip();
			
			this.buf.bindVao();
			this.buf.uploadVboData(0, data, BufferUsage.DYNAMIC_DRAW);
			this.buf.uploadIboData(indices, BufferUsage.DYNAMIC_DRAW);
			
		}
		
		this.updateBuffers = false;
		
	}
	
	private static class StateTracker {
		
		private final State state;
		private int bufferOffset, bufferLength;
		
		public StateTracker(State state) {
			this.state = state;
		}
		
		private float getAutoWidth() {
			return this.state.borderLeft + this.state.borderRight;
		}
		
		private float getAutoHeight() {
			return this.state.borderTop + this.state.borderBottom;
		}
		
	}
	
	public void addState(String name, State state) {
		this.states.put(name, new StateTracker(state));
		this.updateBuffers = true;
	}
	
	public void addStateAndSet(String name, State state) {
		this.addState(name, state);
		this.setState(name);
	}
	
	public void removeState(String name) {
		StateTracker removed = this.states.remove(name);
		if (removed != null) {
			this.updateBuffers = true;
			if (removed == this.currentState) {
				this.currentState = null;
			}
		}
	}
	
	public void clearStates() {
		if (!this.states.isEmpty()) {
			this.states.clear();
			this.updateBuffers = true;
			if (this.currentState != null) {
				this.currentState = null;
			}
		}
	}
	
	/**
	 * Set a state to be the single state and set it.
	 * @param state The unique state.
	 */
	public void setState(State state) {
		if (this.states.size() != 1 || !this.states.containsKey(DEFAULT_STATE)) {
			this.states.clear();
			this.addStateAndSet(DEFAULT_STATE, state);
		}
	}
	
	public void setState(String name) {
		this.currentState = this.states.get(name);
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
	
	public static State newStateTile(MapTexture2D.Tile tile) {
		return new State().setTextureTile(tile);
	}
	
	public static State newState() {
		return new State();
	}
	
	public static class State {
		
		protected int texName, texWidth, texHeight;
		protected float texCoordX, texCoordY, texCoordW = 1, texCoordH = 1;
		protected int borderLeft, borderTop, borderRight, borderBottom;
		
		public boolean isTextureValid() {
			return this.texName > 0;
		}
		
		public void removeTexture() {
			this.texName = 0;
		}
		
		public State setTexture(int textureName, int textureWidth, int textureHeight) {
			this.texName = textureName;
			if (this.texWidth != textureWidth || this.texHeight != textureHeight) {
				this.texWidth = textureWidth;
				this.texHeight = textureHeight;
			}
			return this;
		}
		
		public State setTexture(Texture texture, int textureWidth, int textureHeight) {
			texture.checkValid();
			this.setTexture(texture.getName(), textureWidth, textureHeight);
			return this;
		}
		
		public State setTexture(ResTexture2D texture) {
			this.setTexture(texture, texture.getWidth(), texture.getHeight());
			return this;
		}
		
		public State setTexture(DynTexture2D texture) {
			this.setTexture(texture, texture.getWidth(), texture.getHeight());
			return this;
		}
		
		public State setTextureCoords(float x, float y, float width, float height) {
			this.texCoordX = x;
			this.texCoordY = y;
			this.texCoordW = width;
			this.texCoordH = height;
			return this;
		}
		
		public State resetTextureCoords() {
			this.setTextureCoords(0, 0, 1, 1);
			return this;
		}
		
		public State setTextureTile(MapTexture2D.Tile tile) {
			MapTexture2D map = tile.getMap();
			this.setTexture(map, (int) (map.getWidth() * tile.getWidth()), (int) (map.getHeight() * tile.getHeight()));
			this.setTextureCoords(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
			return this;
		}
		
		public State setBorders(int left, int top, int right, int bottom) {
			this.borderLeft = left;
			this.borderTop = top;
			this.borderRight = right;
			this.borderBottom = bottom;
			return this;
		}
		
	}
	
}
