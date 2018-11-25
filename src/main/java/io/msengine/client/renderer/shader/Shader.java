package io.msengine.client.renderer.shader;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Matrix4f;

import io.msengine.client.renderer.framebuffer.Framebuffer;
import io.msengine.client.renderer.window.Window;

public class Shader {
	
	private final ShaderManager manager;
	private final Framebuffer framebufferIn;
	private final Framebuffer framebufferOut;
	private final Map<String, Framebuffer> auxFramebuffers;
	
	public Shader(ShaderManager manager, Framebuffer framebufferIn, Framebuffer framebufferOut) {
		
		this.manager = manager;
		this.framebufferIn = framebufferIn;
		this.framebufferOut = framebufferOut;
		this.auxFramebuffers = new HashMap<>();
		
	}
	
	public void delete() {
		this.manager.delete();
	}
	
	public void addAuxFramebuffer(String identifier, Framebuffer framebuffer) {
		this.auxFramebuffers.put( identifier, framebuffer );
	}
	
	public void render(float alpha) {
		
		Framebuffer.unbind();
		
		int outWidth = this.framebufferOut.getWidth();
		int outHeight = this.framebufferOut.getHeight();
		
		// glViewport( 0, 0, outWidth, outHeight );
		
		this.manager.setSamplerObject( "diffuse_sampler", this.framebufferIn );
		
		for ( Entry<String, Framebuffer> auxFramebuffer : this.auxFramebuffers.entrySet() ) {
			
			String identifier = auxFramebuffer.getKey();
			Framebuffer framebuffer = auxFramebuffer.getValue();
			
			this.manager.setSamplerObject( identifier, framebuffer );
			this.manager.getShaderUniformOrDefault( "aux_size_" + identifier ).set( (float) framebuffer.getWidth(), (float) framebuffer.getHeight() );
			
		}
		
		this.manager.getShaderUniformOrDefault("projection").set( (Matrix4f) null );
		this.manager.getShaderUniformOrDefault("in_size").set( (float) this.framebufferIn.getWidth(), (float) this.framebufferIn.getHeight() );
		this.manager.getShaderUniformOrDefault("out_size").set( (float) outWidth, (float) outHeight );
		this.manager.getShaderUniformOrDefault("time").set( alpha );
		
		Window window = Window.getInstance();
		this.manager.getShaderUniformOrDefault("screen_size").set( (float) window.getWidth(), (float) window.getHeight() );
		
		this.manager.use();
		
		this.framebufferOut.clear();
		
		// TODO Finish
		
	}
	
}
