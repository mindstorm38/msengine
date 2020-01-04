package io.msengine.client.renderer.shader;

public class ShaderBlockUniform extends ShaderUniformBase {

	private final ShaderUniformBlock block;
	
	public ShaderBlockUniform(ShaderUniformBlock block, String identifier, ShaderValueType type) {
		
		super( identifier, type );
		
		this.block = block;
		
	}

	@Override
	protected void _upload() {
		
	}

	@Override
	public void tryUpload() {
		
	}

}
