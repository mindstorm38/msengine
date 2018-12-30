package io.msengine.client.renderer.shader;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL15.*;

import java.util.ArrayList;
import java.util.List;

import io.msengine.client.renderer.util.RenderConstantFields;

/**
 * 
 * Used to create shared uniform blocks.<br>
 * Works only with <code>std140</code> block layout.
 * 
 * @author Mindstorm38
 * 
 */
public class ShaderUniformBlock implements ShaderUniformHandler {
	
	// Static \\
	
	private static final List<Integer> usedBindings = new ArrayList<>();
	
	// Class \\
	
	protected final String identifier;
	protected int binding;
	
	private int ubo;
	
	private final List<ShaderBlockUniform> uniforms;
	
	public ShaderUniformBlock(String identifier) {
		
		this.identifier = identifier;
		this.binding = -1;
		
		this.ubo = -1;
		
		this.uniforms = new ArrayList<>();
		
	}
	
	public void init() {
		
		if ( this.binding != -1 )
			throw new IllegalStateException("This uniform block is already initied.");
		
		int maximumBindings = RenderConstantFields.getInstance().getMaxUniformBufferBindings();
		
		if ( usedBindings.size() >= maximumBindings )
			throw new IllegalStateException("Can't create more shared uniforms block, too many bindings. (" + maximumBindings + ")" );
		
		this.binding = 0;
		
		while ( usedBindings.contains( this.binding ) )
			this.binding++;
		
		this.ubo = glGenBuffers();
		
		for ( ShaderBlockUniform uniform : this.uniforms ) {
			
			
			
		}
		
	}
	
	public void stop() {
		
		if ( this.binding == -1 )
			throw new IllegalStateException("This uniform block is not inited, can't stop.");

		usedBindings.remove( this.binding );
		this.binding = -1;
		
		glDeleteBuffers( this.ubo );
		this.ubo = -1;
		
	}
	
	private void checkInitied() {
		if ( this.binding != -1 ) throw new IllegalStateException("This uniform block is already initied.");
	}
	
	public boolean usable() {
		return this.binding != -1;
	}
	
	public void checkUsable() {
		if ( !this.usable() ) throw new IllegalStateException("Can't use this uniform block, first init it.");
	}
	
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public ShaderUniformBase getShaderUniformOrDefault(String identifier) {
		for ( ShaderBlockUniform uniform : this.uniforms )
			if ( uniform.usable() && identifier.equals( uniform.identifier ) )
				return uniform;
		return DEFAULT_UNIFORM;
	}

	@Override
	public ShaderUniformBase getShaderUniform(String identifier) {
		for ( ShaderBlockUniform uniform : this.uniforms )
			if ( uniform.usable() && identifier.equals( uniform.identifier ) )
				return uniform;
		return null;
	}
	
	public void registerUniform(String identifier, ShaderValueType type) {
		
		this.checkInitied();
		this.uniforms.add( new ShaderBlockUniform( this, identifier, type ) );
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		if ( this.binding != -1 ) this.stop();
	}
	
}
