package io.msengine.client.renderer.shader;

@Deprecated
public class ShaderUniform extends ShaderUniformBase {

	// Class \\
	
	private final ShaderManager shaderManager;
	
	protected ShaderUniform(ShaderManager shaderManager, String identifier, ShaderValueType type) {
		super(identifier, type);
		this.shaderManager = shaderManager;
	}

	@Override
	protected void _upload() {
		this.getType().upload(this.getLocation(), this.getBuffer());
	}

	@Override
	public void tryUpload() {
		
		// Do not check if buffer is not null in case of default uniform because this function is overridden
		if (this.shaderManager.isCurrent()) {
			this._upload();
		}
		
	}
	
}
