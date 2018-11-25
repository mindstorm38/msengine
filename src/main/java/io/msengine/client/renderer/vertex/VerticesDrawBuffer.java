package io.msengine.client.renderer.vertex;

import static org.lwjgl.opengl.GL11.glDrawArrays;

import io.msengine.client.renderer.shader.ShaderManager;

/**
 * 
 * @author Mindstorm38
 *
 */
public class VerticesDrawBuffer extends DrawBuffer {

	protected int verticesCount;
	
	public VerticesDrawBuffer(ShaderManager shaderManager, VertexArrayFormat format, String...enabledVertexAttribsIdentifiers) {
		
		super( shaderManager, format, enabledVertexAttribsIdentifiers );
		
	}
	
	// - Vertices count
	
	/**
	 * Get vertices count used by {@link #drawArrays(int)}
	 * @return Vertices count
	 */
	public int getVerticesCount() {
		return this.verticesCount;
	}
	
	/**
	 * Set vertices count used by {@link #drawArrays(int)}
	 * @param verticesCount Vertices count
	 * @return Vertices count
	 */
	public int setVerticesCount(int verticesCount) {
		return this.verticesCount = verticesCount;
	}
	
	// - Draw
	
	/**
	 * Drawing vertices using {@link #verticesCount}
	 * @param mode OpenGL primitive type
	 */
	public void drawArrays(int mode) {
		
		this.preDraw();
		
		glDrawArrays( mode, 0, this.verticesCount );
		
		this.postDraw();
		
	}
	
	/**
	 * Use {@link #drawArrays(int)} with {@link DrawBuffer#DEFAULT_PRIMITIVE_TYPE}
	 */
	public void drawArrays() {
		this.drawArrays( DEFAULT_PRIMITIVE_TYPE );
	}

}
