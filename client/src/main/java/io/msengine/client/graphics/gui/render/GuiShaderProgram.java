package io.msengine.client.graphics.gui.render;

import io.msengine.client.EngineClient;
import io.msengine.client.graphics.shader.Shader;
import io.msengine.client.graphics.shader.ShaderProgram;
import io.msengine.client.graphics.shader.ShaderType;

import java.io.IOException;

public class GuiShaderProgram extends ShaderProgram {

	protected Shader createVertexShader() throws IOException {
		return Shader.fromSource(ShaderType.VERTEX, EngineClient.ASSETS.openAssetStreamExcept("mse/shaders/gui.vsh"));
	}
	
	protected Shader createFragmentShader() throws IOException {
		return Shader.fromSource(ShaderType.FRAGMENT, EngineClient.ASSETS.openAssetStreamExcept("mse/shaders/gui.fsh"));
	}
	
	@Override
	protected void preLink() {
		
		super.preLink();
		
		Shader vsh, fsh;
		
		try {
			vsh = this.createVertexShader();
			fsh = this.createFragmentShader();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create GUI shaders.", e);
		}
		
		vsh.compile();
		fsh.compile();
		
		this.attachShader(vsh);
		this.attachShader(fsh);
		
	}
	
}
