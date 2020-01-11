package io.msengine.client.gui;

import io.msengine.client.renderer.texture.TextureMapTile;
import io.msengine.client.renderer.texture.TextureObject;
import io.msengine.client.renderer.util.BufferUsage;
import io.msengine.client.renderer.util.BufferUtils;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.sutil.CollectionUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import static io.msengine.client.renderer.vertex.type.GuiFormat.*;

/**
 *
 * TODO - Mettre cette classe en abstract, et créer une nouvelle classe "GuiLineComponentTexture" qui va avoir plus qu'une
 * TODO seul "map tile" par état (state, d'ailleur laisser le state ici) et une variable en plus pour définir la largeur de
 * TODO la "fixed tile". Et aussi plus de variable "scale", mais utiliser le ratio de la hauteur demandé par rapport à la
 * TODO hauteur de la texture.
 * TODO - Implementer dans cette classe getAutoWidth à la valeur minimal de "2x(taille fixe)" (2 car il faut bien affiché les bords).
 *
 */
public class GuiComponentTexture extends GuiObject {
	
	private final Type type;
	
	protected IndicesDrawBuffer buffer;
	protected boolean updateVertices;
	protected boolean updateTexCoords;
	
	protected final Map<String, Map<TextureComponent, TextureMapTile>> statesTiles;
	protected final HashSet<String> statesReady;
	protected TextureObject texture;
	protected String state;
	protected float scale;
	
	public GuiComponentTexture(Type type) {
		
		this.type = Objects.requireNonNull(type);
		
		this.statesTiles = new HashMap<>();
		this.statesReady = new HashSet<>();
		
	}
	
	public GuiComponentTexture() {
		this(Type.TWO_COMPONENT_LINE);
	}
	
	@Override
	protected void init() {
		
		this.buffer = this.renderer.createDrawBuffer( false, true );
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
			
			int posCount, texCount;
			
			switch (this.type) {
				case TWO_COMPONENT_LINE:
					indicesBuffer = MemoryUtil.memAllocInt(this.buffer.setIndicesCount(12));
					indicesBuffer.put(0).put(1).put(3);
					indicesBuffer.put(1).put(2).put(3);
					indicesBuffer.put(4).put(5).put(7);
					indicesBuffer.put(5).put(6).put(7);
					posCount = 16;
					texCount = 16;
					break;
				default: throw new UnsupportedOperationException();
			}
			
			indicesBuffer.flip();
			
			this.buffer.bindVao();
			this.buffer.allocateVboData(GUI_POSITION, posCount << 2, BufferUsage.DYNAMIC_DRAW);
			this.buffer.allocateVboData(GUI_TEX_COORD, texCount << 2, BufferUsage.DYNAMIC_DRAW);
			this.buffer.uploadIboData(indicesBuffer, BufferUsage.STATIC_DRAW);
			
		} finally {
			BufferUtils.safeFree(indicesBuffer);
		}
		
		this.updateVerticesBuffer();
		this.updateTexCoordsBuffer();
		
	}
	
	private void updateVerticesBuffer() {
		
		if (this.isStateReady()) {
			
			FloatBuffer verticesBuffer = null;
			
			try {
				
				switch (this.type) {
					case TWO_COMPONENT_LINE:
						
						TextureMapTile fixTile = this.getComponentTile(TextureComponent.FIX_COMPONENT);
						
						float fixWidth = this.texture.getWidth() * fixTile.width * this.scale;
						float height = this.height;//this.texture.getHeight() * fixTile.height;
						float growingWidth = this.width - fixWidth;
						
						verticesBuffer = MemoryUtil.memAllocFloat(16);
						
						verticesBuffer.put(0).put(0);
						verticesBuffer.put(0).put(height);
						verticesBuffer.put(growingWidth).put(height);
						verticesBuffer.put(growingWidth).put(0);
						verticesBuffer.put(growingWidth).put(0);
						verticesBuffer.put(growingWidth).put(height);
						verticesBuffer.put(this.width).put(height);
						verticesBuffer.put(this.width).put(0);
						
						break;
					default:
						throw new UnsupportedOperationException();
				}
				
				verticesBuffer.flip();
				
				this.buffer.bindVao();
				this.buffer.uploadVboSubData(GUI_POSITION, 0, verticesBuffer);
				
			} finally {
				BufferUtils.safeFree(verticesBuffer);
			}
			
		}
		
		this.updateVertices = false;
		
	}
	
	private void updateTexCoordsBuffer() {
		
		if (this.isStateReady()) {
			
			FloatBuffer texCoordsBuffer = null;
			
			try {
				
				switch (this.type) {
					case TWO_COMPONENT_LINE:
						
						TextureMapTile growingTile = this.getComponentTile(TextureComponent.GROWING_COMPONENT);
						TextureMapTile fixTile = this.getComponentTile(TextureComponent.FIX_COMPONENT);
						
						float remainWidth = this.width - (this.texture.getWidth() * fixTile.width * this.scale);
						float growingTexWidth = growingTile.width * (remainWidth / (this.texture.getWidth() * growingTile.width));
						
						texCoordsBuffer = MemoryUtil.memAllocFloat(16);
						
						texCoordsBuffer.put(growingTile.x).put(growingTile.y);
						texCoordsBuffer.put(growingTile.x).put(growingTile.y + growingTile.height);
						texCoordsBuffer.put(growingTile.x + growingTexWidth).put(growingTile.y + growingTile.height);
						texCoordsBuffer.put(growingTile.x + growingTexWidth).put(growingTile.y);
						
						texCoordsBuffer.put(fixTile.x).put(fixTile.y);
						texCoordsBuffer.put(fixTile.x).put(fixTile.y + fixTile.height);
						texCoordsBuffer.put(fixTile.x + fixTile.width).put(fixTile.y + fixTile.height);
						texCoordsBuffer.put(fixTile.x + fixTile.width).put(fixTile.y);
						
						break;
					default:
						throw new UnsupportedOperationException();
				}
				
				texCoordsBuffer.flip();
				
				this.buffer.bindVao();
				this.buffer.uploadVboSubData(GUI_TEX_COORD, 0, texCoordsBuffer);
				
			} finally {
				BufferUtils.safeFree(texCoordsBuffer);
			}
			
		}
		
		this.updateTexCoords = false;
		
	}
	
	@Override
	public void render(float alpha) {
	
		if (!this.isStateReady())
			return;
		
		if (this.updateVertices)
			this.updateVerticesBuffer();
		
		if (this.updateTexCoords)
			this.updateTexCoordsBuffer();
		
		this.model.push().translate(this.xOffset, this.yOffset).apply();
			this.renderer.setTextureSampler(this.texture);
				this.buffer.drawElements();
			this.renderer.resetTextureSampler();
		this.model.pop();
		
	}
	
	@Override
	public void update() {
	
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.updateVertices = true;
		this.updateTexCoords = true;
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.updateVertices = true;
		this.updateTexCoords = true;
	}
	
	public void setComponentTexture(String state, TextureComponent component, TextureMapTile tile) {
		
		if (!this.type.hasComponent(component))
			throw new IllegalArgumentException("This component '" + component.name() + "' is not valid for this ComponentTexture of type '" + this.type.name() + "'.");
		
		if (this.texture != null && this.texture != tile.map.getTextureObject())
			throw new IllegalArgumentException("Invalid tile texture, must be the same as defined previously.");
		
		if (this.texture == null)
			this.texture = tile.map.getTextureObject();
		
		Map<TextureComponent, TextureMapTile> tiles = this.statesTiles.computeIfAbsent(state, st -> new HashMap<>());
		tiles.put(component, tile);
		
		if (tiles.size() == this.type.components.length) {
			
			this.statesReady.add(state);
			this.updateVertices = true;
			this.updateTexCoords = true;
			
		}
		
	}
	
	/**
	 * Internal method to get a component texture map tile for the current state.
	 * @param component The component to get.
	 * @return Map tile corresponding to this component.
	 */
	private TextureMapTile getComponentTile(TextureComponent component) {
		
		if (this.statesTiles.containsKey(this.state)) {
			return this.statesTiles.get(this.state).get(component);
		} else {
			throw new IllegalStateException("This texture component '" + component.name() + "' is not ready in the current state '" + this.state + "'.");
		}
		
	}
	
	/**
	 * Check if the current state is ready for displaying (all textures components are set).
	 * @return True if the state is ready
	 */
	public boolean isStateReady() {
		return this.state != null && this.statesReady.contains(this.state);
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String state) {
		this.state = state;
		this.updateTexCoords = true;
	}
	
	public float getScale() {
		return this.scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		this.updateVertices = true;
		this.updateTexCoords = true;
	}
	
	public enum TextureComponent {
		GROWING_COMPONENT,
		FIX_COMPONENT
	}
	
	public enum Type {
		
		TWO_COMPONENT_LINE (TextureComponent.GROWING_COMPONENT, TextureComponent.FIX_COMPONENT);
		
		private final TextureComponent[] components;
		
		Type(TextureComponent...components) {
			this.components = components;
		}
		
		public boolean hasComponent(TextureComponent component) {
			return CollectionUtils.arrayContains(this.components, component);
		}
		
	}
	
}
