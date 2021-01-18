package io.msengine.client.renderer.gui;

import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

/**
 * @deprecated Consider using the new package {@link io.msengine.client.graphics}.
 */
@Deprecated
public abstract class GuiMask {

	protected final GuiRenderer renderer;
	protected final ModelHandler model;
	
	protected IndicesDrawBuffer buffer;
	
	public GuiMask() {
		
		this.renderer = GuiRenderer.getInstance();
		this.model = this.renderer.model();
		
	}
	
	public void init() {
		this.buffer = this.renderer.createDrawBuffer( false, false );
	}
	
	public void draw() {
		this.buffer.drawElements();
	}
	
	public void stop() {
		
		this.buffer.delete();
		this.buffer = null;
		
	}
	
}
