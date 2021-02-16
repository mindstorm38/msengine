package io.msengine.client.renderer.texture;

@Deprecated
public class TextureMapTile {
	
	public final TextureMapBase<?> map;
	
	public final float x;
	public final float y;
	public final float width;
	public final float height;
	
	TextureMapTile(TextureMapBase<?> map, float x, float y, float width, float height) {
		
		this.map = map;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
	}
	
	@Override
	public String toString() {
		return "TextureMapTile(x:" + this.x + ", y:" + this.y + ", width:" + this.width + ", height:" + this.height + ")";
	}
	
}
