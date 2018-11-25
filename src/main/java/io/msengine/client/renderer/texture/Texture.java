package io.msengine.client.renderer.texture;

import java.io.IOException;

import io.msengine.client.renderer.shader.ShaderSamplerObject;
import io.msengine.common.resource.ResourceManager;

public abstract class Texture implements ShaderSamplerObject {
	
	// Class \\
	
	protected TextureObject texture;
	
	public abstract void loadTexture(ResourceManager resourceManager) throws IOException;
	
	public TextureObject getTextureObject() {
		return this.texture;
	}
	
	public int getId() {
		return this.texture.getId();
	}
	
	@Override
	public int getSamplerId() {
		return this.texture.getSamplerId();
	}
	
	public int getWidth() {
		return this.texture.getWidth();
	}
	
	public int getHeight() {
		return this.texture.getHeight();
	}
	
	public void bind() {
		this.texture.bind();
	}
	
	public void bind(int active) {
		this.texture.bind( active );
	}
	
	public void delete() {
		if ( this.texture != null ) {
			this.texture.delete();
			this.texture = null;
		}
	}
	
}
