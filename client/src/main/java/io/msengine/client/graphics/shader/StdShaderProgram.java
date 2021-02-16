package io.msengine.client.graphics.shader;

import io.msengine.common.asset.Asset;

import java.io.IOException;
import java.util.Objects;

public class StdShaderProgram extends ShaderProgram {

	protected final Asset vshAsset;
	protected final Asset fshAsset;
	
	public StdShaderProgram(Asset vshAsset, Asset fshAsset) {
		this.vshAsset = Objects.requireNonNull(vshAsset);
		this.fshAsset = Objects.requireNonNull(fshAsset);
	}
	
	protected Shader createVertexShader() throws IOException {
		return Shader.fromSource(ShaderType.VERTEX, this.vshAsset.openStreamExcept());
	}
	
	protected Shader createFragmentShader() throws IOException {
		return Shader.fromSource(ShaderType.FRAGMENT, this.fshAsset.openStreamExcept());
	}
	
	@Override
	protected void preLink() {
		
		super.preLink();
		
		Shader vsh, fsh;
		
		try {
			vsh = this.createVertexShader();
			fsh = this.createFragmentShader();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create shaders.", e);
		}
		
		vsh.compile();
		fsh.compile();
		
		this.attachShader(vsh);
		this.attachShader(fsh);
		
	}
	
	@Override
	protected void postLink() {
		super.postLink();
		this.deleteAttachedShaders();
	}

}
