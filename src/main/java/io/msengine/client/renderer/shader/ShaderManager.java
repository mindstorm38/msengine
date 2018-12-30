package io.msengine.client.renderer.shader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import io.msengine.client.renderer.texture.TextureObject;
import io.msengine.client.renderer.vertex.VertexElement;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL31.*;
import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * @author Mindstorm38
 *
 */
public class ShaderManager implements ShaderUniformHandler {
	
	// Static \\
	
	private static ShaderManager currentShaderManager = null; 
	private static int currentProgram = -1;
	
	// Class \\
	
	private final String identifier;
	private final String vertexShaderIdentifier;
	private final String fragmentShaderIdentifier;
	
	private final List<ShaderSampler> samplers;
	private final List<ShaderUniform> uniforms;
	private final List<ShaderAttribute> attributes;
	
	private final List<ShaderUniformBlock> uniformBlocks;
	
	private final List<ShaderAttribute> attributesView;
	
	private boolean built;
	
	protected int program;
	private ShaderLoader vertexShaderLoader;
	private ShaderLoader fragmentShaderLoader;
	
	public ShaderManager(String identifier, String vertexShaderIdentifier, String fragmentShaderIdentifier) {
		
		this.identifier = identifier;
		this.vertexShaderIdentifier = vertexShaderIdentifier;
		this.fragmentShaderIdentifier = fragmentShaderIdentifier;
		
		this.samplers = new ArrayList<>();
		this.uniforms = new ArrayList<>();
		this.attributes = new ArrayList<>();
		
		this.uniformBlocks = new ArrayList<>();
		
		this.attributesView = Collections.unmodifiableList( this.attributes );
		
		this.reset();
		
	}
	
	public ShaderManager(String identifier) {
		this( identifier, identifier, identifier );
	}
	
	private void reset() {
		
		this.built = false;
		this.program = -1;
		this.vertexShaderLoader = null;
		this.fragmentShaderLoader = null;
		
	}
	
	public boolean wasBuilt() {
		return this.built;
	}
	
	public void checkBuilt() {
		if ( this.built ) throw new IllegalStateException("This shader is already built");
	}
	
	public void checkNotBuilt() {
		if ( !this.built ) throw new IllegalStateException("This shader isn't built");
	}
	
	public ShaderManager registerSampler(String identifier) {
		
		this.checkBuilt();
		this.samplers.add( new ShaderSampler( identifier ) );
		return this;
		
	}
	
	public ShaderManager registerUniform(String identifier, ShaderValueType type) {
		
		this.checkBuilt();
		this.uniforms.add( new ShaderUniform( this, identifier, type ) );
		return this;
		
	}
	
	public ShaderManager registerAttribute(VertexElement vertexElement) {
		
		this.checkBuilt();
		
		ShaderAttribute attrib = this.getShaderAttribute( vertexElement );
		
		if ( attrib != null )
			throw new IllegalArgumentException( "This vertex element '" + vertexElement.toString() + " is already used by an attribute" );
		
		this.attributes.add( new ShaderAttribute( vertexElement ) );
		
		return this;
		
	}
	
	public ShaderManager boundUniformBlock(ShaderUniformBlock uniformBlock) {
		
		this.checkBuilt();
		
		if ( this.uniformBlocks.contains( uniformBlock ) )
			throw new IllegalArgumentException( "This uniform block '" + uniformBlock.getIdentifier() + "' is already bound to this shader" );
		
		this.uniformBlocks.add( uniformBlock );
		
		return this;
		
	}
	
	private void checkLinkStatus() {
		if ( glGetProgrami( this.program, GL_LINK_STATUS ) != GL_TRUE )
			throw new ShaderBuildException( glGetProgramInfoLog( this.program ) );
	}
	
	private void checkValidateStatus() {
		if ( glGetProgrami( this.program, GL_VALIDATE_STATUS ) != GL_TRUE )
			throw new ShaderBuildException( glGetProgramInfoLog( this.program ) );
	}
	
	public void build() {
		
		this.checkBuilt();
		
		this.program = glCreateProgram();
		
		try {
			
			this.vertexShaderLoader = ShaderLoader.load( ShaderType.VERTEX, this.vertexShaderIdentifier );
			this.fragmentShaderLoader = ShaderLoader.load( ShaderType.FRAGMENT, this.fragmentShaderIdentifier );
			
		} catch (Exception e) {
			throw new ShaderBuildException( "Failed to build shader manager, one of shader loader failed", e );
		}
		
		this.vertexShaderLoader.attachShader( this );
		this.fragmentShaderLoader.attachShader( this );
		
		glLinkProgram( this.program );
		this.checkLinkStatus();
		
		this.vertexShaderLoader.detachShader( this );
		this.fragmentShaderLoader.detachShader( this );
		
		glValidateProgram( this.program );
		this.checkValidateStatus();
		
		this.setupUniforms();
		this.setupAttributes();
		this.setupUniformBlocks();
		
		this.built = true;
		
	}
	
	private void setupUniforms() {
		
		List<ShaderSampler> removedSamplers = new ArrayList<>();
		
		for ( ShaderSampler sampler : this.samplers ) {
			
			int location = glGetUniformLocation( this.program, sampler.identifier );
			
			if ( location == -1 ) {
				
				LOGGER.warning( "Could not find sampler '" + sampler.identifier + "' in the program '" + this.identifier + "'." );
				removedSamplers.add( sampler );
				
			} else {
				
				sampler.location = location;
				
			}
			
		}
		
		this.samplers.removeAll( removedSamplers );
		
		for ( ShaderUniform uniform : this.uniforms ) {
			
			int location = glGetUniformLocation( this.program, uniform.identifier );
			
			if ( location == -1 ) {
				
				LOGGER.warning( "Could not find uniform '" + uniform.identifier + "' in the program '" + this.identifier + "'." );
				
			} else {
				
				uniform.location = location;
				uniform.init();
				
			}
			
		}
		
	}
	
	private void setupAttributes() {
		
		for ( ShaderAttribute attribute : this.attributes ) {
			
			int location = glGetAttribLocation( this.program, attribute.identifier );
			
			if ( location == -1 ) {
				
				LOGGER.warning( "Could not find attribute '" + attribute.identifier + "' in the program '" + this.identifier + "'." );
				
			} else {
				
				attribute.location = location;
				
			}
			
		}
		
	}
	
	private void setupUniformBlocks() {
		
		for ( ShaderUniformBlock block : this.uniformBlocks ) {
			
			if ( !block.usable() ) {
				
				LOGGER.warning( "Could not use uniform block '" + block.identifier + "' in the program '" + this.identifier + "', initialize it first." );
				continue;
				
			}
			
			int index = glGetUniformBlockIndex( this.program, block.identifier );
			
			if ( index == -1 ) {
				
				LOGGER.warning( "Could not find uniform block '" + block.identifier + "' in the program '" + this.identifier + "'." );
				
			} else {
				
				glUniformBlockBinding( this.program, index, block.binding );
				
			}
			
		}
		
	}
	
	public void use() {
		
		this.checkNotBuilt();
		
		currentShaderManager = this;
		
		if ( this.program != currentProgram ) {
			
			glUseProgram( this.program );
			currentProgram = this.program;
			
		}
		
		for ( int i = 0; i < this.samplers.size(); i++ ) {
			
			ShaderSampler sampler = this.samplers.get( i );
			
			if ( sampler.object != null ) {
				
				TextureObject.bindTexture( sampler.object.getSamplerId(), i );
				glUniform1i( sampler.location, i );
				
			}
			
		}
		
		for ( ShaderUniform uniform : this.uniforms )
			if ( uniform.usable() )
				uniform.upload();
		
	}
	
	public boolean isCurrent() {
		return currentShaderManager == this;
	}
	
	public void end() {
		
		this.checkNotBuilt();
		
		glUseProgram( 0 );
		currentProgram = -1;
		currentShaderManager = null;
		
		for ( int i = 0; i < this.samplers.size(); i++ )
			if ( this.samplers.get( i ).object != null )
				TextureObject.unbind( i );
		
	}
	
	public void delete() {
		
		if ( !this.built ) return;
		
		this.vertexShaderLoader.releaseShader();
		this.fragmentShaderLoader.releaseShader();
		glDeleteProgram( this.program );
		
		for ( ShaderUniform uniform : this.uniforms )
			if ( uniform.usable() )
				uniform.delete();
		
		this.reset();
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.delete();
	}
	
	@Override
	public ShaderUniformBase getShaderUniformOrDefault(String identifier) {
		for ( ShaderUniform uniform : this.uniforms )
			if ( uniform.usable() && uniform.identifier.equals( identifier ) )
				return uniform;
		return DEFAULT_UNIFORM;
	}

	@Override
	public ShaderUniformBase getShaderUniform(String identifier) {
		for ( ShaderUniform uniform : this.uniforms )
			if ( uniform.usable() && uniform.identifier.equals( identifier ) )
				return uniform;
		return null;
	}
	
	public void setSamplerObject(String identifier, ShaderSamplerObject object) {
		
		ShaderSampler sampler;
		
		for ( int i = 0; i < this.samplers.size(); i++ ) {
			
			sampler = this.samplers.get( i );
			
			if ( sampler.identifier.equals( identifier ) ) {
				
				sampler.object = object;
				
				if ( this.isCurrent() ) {
					
					if ( object == null ) {
						TextureObject.unbind( i );
					} else {
						TextureObject.bindTexture( sampler.object.getSamplerId(), i );
					}
					
				}
				
				return;
				
			}
			
		}
		
		throw new IllegalArgumentException( "Can't find sampler named '" + identifier + "' in shader manager '" + this.identifier + "'" );
		
	}
	
	public ShaderAttribute getShaderAttribute(String identifier) {
		for ( ShaderAttribute attribute : this.attributes )
			if ( attribute.identifier.equals( identifier ) )
				return attribute;
		return null;
	}
	
	public ShaderAttribute getShaderAttribute(VertexElement vertexElement) {
		for ( ShaderAttribute attribute : this.attributes )
			if ( attribute.vertexElement.equals( vertexElement ) )
				return attribute;
		return null;
	}
	
	public int getShaderAttributeIndex(String identifier) {
		for ( int i = 0; i < this.attributes.size(); i++ )
			if ( this.attributes.get( i ).identifier.equals( identifier ) )
				return i;
		return -1;
	}
	
	public int getShaderAttributeIndex(ShaderAttribute attribute) {
		return this.attributes.indexOf( attribute );
	}
	
	public int getShaderAttributeLocation(VertexElement vertexElement) {
		for ( ShaderAttribute attribute : this.attributes )
			if ( attribute.vertexElement.equals( vertexElement ) )
				return attribute.location;
		return -1;
	}
	
	public int getShaderAttributeLocation(String identifier) {
		for ( ShaderAttribute attribute : this.attributes )
			if ( attribute.identifier.equals( identifier ) )
				return attribute.location;
		return -1;
	}
	
	public void getShaderAttributeLocationSafe(VertexElement vertexElement, Consumer<Integer> locationConsumer) {
		int location = this.getShaderAttributeLocation( vertexElement );
		if ( location != -1 ) locationConsumer.accept( location );
	}
	
	public List<ShaderAttribute> getShaderAttributes() {
		return this.attributesView;
	}
	
	public int getShaderAttributeCount() {
		return this.attributes.size();
	}
	
	public ShaderUniformBlock getUniformBlock(String identifier) {
		for ( ShaderUniformBlock block : this.uniformBlocks )
			if ( identifier.equals( block.getIdentifier() ) )
				return block;
		return null;
	}
	
	public int getProgram() {
		return this.program;
	}
	
	public ShaderLoader getVertexShaderLoader() {
		return this.vertexShaderLoader;
	}
	
	public ShaderLoader getFragmentShaderLoader() {
		return this.fragmentShaderLoader;
	}
	
}
