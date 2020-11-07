package io.msengine.client.graphics.gui.render;

import io.msengine.client.EngineClient;
import io.msengine.client.graphics.shader.StdShaderProgram;
import io.msengine.client.graphics.shader.uniform.FloatMatrix4Uniform;
import io.msengine.common.asset.Asset;
import org.joml.Matrix4f;

public class GuiStdProgramBase extends StdShaderProgram implements GuiStdProgram {
	
	protected static final Asset COMMON_VERTEX_SHADER = EngineClient.ASSETS.getAsset("mse/shaders/gui.vsh");
	protected static final Asset MAIN_FRAGMENT_SHADER = EngineClient.ASSETS.getAsset("mse/shaders/gui.fsh");
	protected static final Asset TEXT_FRAGMENT_SHADER = EngineClient.ASSETS.getAsset("mse/shaders/gui_text.fsh");
	
	// Class //
	
	protected FloatMatrix4Uniform projectionMatrixUniform;
	protected FloatMatrix4Uniform modelMatrixUniform;
	protected int attribPosition;
	
	public GuiStdProgramBase(Asset vshAsset, Asset fshAsset) {
		super(vshAsset, fshAsset);
	}
	
	@Override
	protected void postLink() {
		
		super.postLink();
		
		this.projectionMatrixUniform = this.createUniform("projection_matrix", FloatMatrix4Uniform::new);
		this.modelMatrixUniform = this.createUniform("model_matrix", FloatMatrix4Uniform::new);
		this.attribPosition = this.getAttribLocation("position");
		
	}
	
	@Override
	public void setProjectionMatrix(Matrix4f mat) {
		this.projectionMatrixUniform.set(mat);
	}
	
	@Override
	public void uploadProjectionMatrix() {
		this.projectionMatrixUniform.uploadIfChanged();
	}
	
	@Override
	public void uploadModelMatrix(Matrix4f mat) {
		this.modelMatrixUniform.set(mat);
		this.modelMatrixUniform.uploadIfChanged();
	}
	
}
