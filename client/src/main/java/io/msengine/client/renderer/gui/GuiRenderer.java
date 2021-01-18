package io.msengine.client.renderer.gui;

import org.joml.Matrix4f;

import io.msengine.client.renderer.model.ModelApplyListener;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.shader.ShaderSamplerObject;
import io.msengine.client.renderer.util.BlendMode;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.common.util.Color;
import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;

import static org.lwjgl.opengl.GL11.*;

/**
 * @deprecated Consider using the new package {@link io.msengine.client.graphics}.
 */
@Deprecated
public class GuiRenderer implements ModelApplyListener {
	
	// Singleton \\
	
	private static GuiRenderer INSTANCE;
	
	public static GuiRenderer getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( GuiRenderer.class );
		return INSTANCE;
	}
	
	// Class \\
	
	// private final ResourceManager resourceManager;
	
	private boolean rendering;
	
	// - Shader program
	private final GuiShaderManager shaderManager;
	
	// - Global color
	private final Color globalColor;
	
	// - Model matrix handler
	private final ModelHandler modelHandler;
	
	// - Stencil mask
	private boolean masking;
	
	// - Transformation matrices
	private final Matrix4f globalMatrix;
	private final Matrix4f projectionMatrix;
	private Matrix4f modelMatrix;
	
	public GuiRenderer() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( GuiRenderer.class );
		INSTANCE = this;
		
		this.rendering = false;
		
		// - Shader
		this.shaderManager = new GuiShaderManager();
		
		// - Global color
		this.globalColor = new Color();
		
		// - Model matrix handler
		this.modelHandler = new ModelHandler( this );
		
		// - Stencil mask
		this.masking = false;
		
		// - Transformation matrices
		this.globalMatrix = new Matrix4f();
		this.projectionMatrix = new Matrix4f();	
		this.modelMatrix = null;
		
	}
	
	public void checkRendering() {
		if ( !this.rendering ) throw new IllegalStateException( "Execute GuiRenderer.beginRender() first" );
	}
	
	public IndicesDrawBuffer createDrawBuffer(boolean color, boolean texture) {
		return this.shaderManager.createGuiDrawBuffer( color, texture );
	}
	
	public Color getGlobalColor() {
		return this.globalColor;
	}
	
	public void setGlobalColor(float r, float g, float b, float a) {
		this.globalColor.setAll( r, g, b, a );
		this.applyGlobalColor();
	}
	
	public void setGlobalColor(float r, float g, float b) {
		this.globalColor.setAll( r, g, b );
		this.applyGlobalColor();
	}
	
	public void setGlobalColor(Color color) {
		this.globalColor.setAll( color );
		this.applyGlobalColor();
	}
	
	public void applyGlobalColor() {
		this.shaderManager.setGlobalColor( this.globalColor );
	}
	
	public void resetGlobalColor() {
		
		this.globalColor.setAll( Color.WHITE );
		this.shaderManager.setGlobalColor( Color.WHITE );
		
	}
	
	private void calculateGlobalMatrix() {
		
		this.globalMatrix.set( this.projectionMatrix );
		if ( this.modelMatrix != null ) this.globalMatrix.mul( this.modelMatrix );
		
		this.shaderManager.setGlobalMatrix( this.globalMatrix );
	
	}
	
	public void setTextureSampler(ShaderSamplerObject sampler) {
		this.shaderManager.setTextureSampler( sampler );
	}
	
	public void resetTextureSampler() {
		this.setTextureSampler( null );
	}
	
	public void init() {
		
		// - Shader
		this.shaderManager.build();
		
	}
	
	public void stop() {
		
		// - Shader
		this.shaderManager.delete();
		
	}
	
	public void beginRender() {
		
		if ( this.rendering ) throw new IllegalStateException( "GuiRenderer is already rendering" );
		
		glEnable(GL_BLEND);
		BlendMode.TRANSPARENCY.use();
		
		this.shaderManager.use();
		
		this.rendering = true;
		this.masking = false;
		
		this.resetGlobalColor();
		this.calculateGlobalMatrix();
		
	}
	
	public void endRender() {
		
		this.shaderManager.end();
		
		if ( this.masking ) this.unmask();
		this.rendering = false;
		
	}
	
	public void mask(GuiMask[] masks) {
		
		this.checkRendering();
		
		if ( this.masking ) return;
		this.masking = true;
		
		this.resetTextureSampler();
		
		glEnable( GL_STENCIL_TEST );
		
		glStencilMask( 1 );
		glStencilFunc( GL_ALWAYS, 1, 1 );
		glStencilOp( GL_KEEP, GL_KEEP, GL_REPLACE );
		glColorMask( false, false, false, false );
		
		for (GuiMask mask : masks) {
			mask.draw();
		}
		
		glStencilMask( 1 );
		glStencilFunc( GL_EQUAL, 1, 1 );
		glStencilOp( GL_KEEP, GL_KEEP, GL_KEEP );
		glColorMask( true, true, true, true );
		
	}
	
	public void unmask() {
		
		this.checkRendering();
		
		if ( !this.masking ) return;
		this.masking = false;
		
		glDisable( GL_STENCIL_TEST );
		
	}
	
	public void updateRenderSize(int width, int height) {
		
		this.projectionMatrix.identity();
		this.projectionMatrix.ortho( 0, width, height, 0, 1024, -1024 );
		this.calculateGlobalMatrix();
		
	}
	
	public ModelHandler model() {
		return this.modelHandler;
	}

	@Override
	public void modelApply(Matrix4f model) {
		
		this.modelMatrix = model;
		this.calculateGlobalMatrix();
		
	}

}
