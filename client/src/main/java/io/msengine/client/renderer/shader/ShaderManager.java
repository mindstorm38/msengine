package io.msengine.client.renderer.shader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.msengine.client.renderer.vertex.VertexElement;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL31.*;
import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * This class is used to manage a group of both vertex and fragment
 * shaders. And then
 *
 * TODO : Rework this class to use Maps to access samplers, uniforms and attributes faster.
 *
 * @author Théo Rozier (Mindstorm38)
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
	
	private final Map<String, ShaderSampler> samplers = new HashMap<>();
	private final Map<String, ShaderUniform> uniforms = new HashMap<>();
	private final Map<String, ShaderAttribute> attributes = new HashMap<>();
	private final Map<VertexElement, ShaderAttribute> attributesByElts = new HashMap<>();
	
	private final Map<String, ShaderUniformBlock> uniformBlocks = new HashMap<>();
	
	private boolean built;
	
	protected int program;
	private ShaderLoader vertexShaderLoader;
	private ShaderLoader fragmentShaderLoader;
	
	public ShaderManager(String identifier, String vertexShaderIdentifier, String fragmentShaderIdentifier) {
		
		this.identifier = identifier;
		this.vertexShaderIdentifier = vertexShaderIdentifier;
		this.fragmentShaderIdentifier = fragmentShaderIdentifier;
		
		this.reset();
		
	}
	
	/**
	 * Constructor creating a shader manager with same vertex and fragment shaders identifiers as the shader identifier.
	 * @param identifier The identifier of vertex, fragement shaders and the identifier of this object.
	 */
	public ShaderManager(String identifier) {
		this( identifier, identifier, identifier );
	}
	
	/**
	 * Common method for constructor and {@link #delete()}.<br>
	 * Reset fields : buitt, program, shaderLoaders.
	 */
	private void reset() {
		
		this.built = false;
		this.program = -1;
		this.vertexShaderLoader = null;
		this.fragmentShaderLoader = null;
		
	}
	
	/**
	 * @return If the shader was built.
	 */
	public boolean wasBuilt() {
		return this.built;
	}
	
	/**
	 * @throws IllegalStateException If the shader was already built.
	 */
	public void checkBuilt() {
		if ( this.built ) throw new IllegalStateException("This shader is already built");
	}
	
	/**
	 * @throws IllegalStateException If the shader isn't built.
	 */
	public void checkNotBuilt() {
		if ( !this.built ) throw new IllegalStateException("This shader isn't built");
	}
	
	/**
	 * Register a texture sampler (2D) from its identifier.<br>
	 * Reminder : Samplers are simples 'sampler2D' uniforms in shaders.
	 * @param identifier Sampler uniform identifier in shader.
	 * @return This manager instance.
	 */
	public ShaderManager registerSampler(String identifier) {
		
		this.checkBuilt();
		this.samplers.put(identifier, new ShaderSampler(identifier));
		return this;
		
	}
	
	/**
	 * Register an uniform from its identifier and its type.
	 * @param identifier Uniform identifier in shader.
	 * @param type Uniform data type.
	 * @return This manager instance.
	 */
	public ShaderManager registerUniform(String identifier, ShaderValueType type) {
		
		this.checkBuilt();
		this.uniforms.put(identifier, new ShaderUniform(this, identifier, type));
		return this;
		
	}
	
	/**
	 * Register an attribute from a VertexElement.<br>
	 * <u>Note that</u> the given element will not be mutated by the manager,
	 * so it can be a constant, like {@link VertexElement#POSITION_2F}.
	 * @param vertexElement The vertex element to add.
	 * @return This manager instance.
	 */
	public ShaderManager registerAttribute(VertexElement vertexElement) {
		
		this.checkBuilt();
		
		if (this.attributesByElts.containsKey(vertexElement)) {
			throw new IllegalArgumentException("This vertex element '" + vertexElement.toString() + " is already used by an attribute");
		}
		
		ShaderAttribute attr = new ShaderAttribute(vertexElement);
		this.attributes.put(attr.getIdentifier(), attr);
		this.attributesByElts.put(vertexElement, attr);
		
		return this;
		
	}
	
	/**
	 * <u><b>WORK IN PROGRESS</b></u><br>
	 * Bind a uniform block to the shader.
	 * @param uniformBlock The uniform block to bind.
	 * @return This manager instance.
	 */
	public ShaderManager bindUniformBlock(ShaderUniformBlock uniformBlock) {
		
		this.checkBuilt();
		
		if (this.uniformBlocks.containsKey(uniformBlock.identifier)) {
			throw new IllegalArgumentException("This uniform block '" + uniformBlock.identifier + "' is already bound to this shader.");
		}
		
		this.uniformBlocks.put(uniformBlock.identifier, uniformBlock);
		
		return this;
		
	}
	
	/**
	 * @throws ShaderBuildException If program not successfuly linked.
	 */
	private void checkLinkStatus() {
		
		if ( glGetProgrami( this.program, GL_LINK_STATUS ) != GL_TRUE )
			throw new ShaderBuildException( glGetProgramInfoLog( this.program ) );
		
	}
	
	/**
	 * @throws ShaderBuildException If program not successfuly validated.
	 */
	private void checkValidateStatus() {
		
		if ( glGetProgrami( this.program, GL_VALIDATE_STATUS ) != GL_TRUE )
			throw new ShaderBuildException( glGetProgramInfoLog( this.program ) );
		
	}
	
	/**
	 * Build the shader manager, once built no sampler, no attribute and no uniforms can be added to it.
	 */
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
	
	/**
	 * Internal method to setup uniforms to GL.
	 */
	private void setupUniforms() {
		
		AtomicInteger activeTexture = new AtomicInteger(0);
		
		this.samplers.values().removeIf(sampler -> {
			
			int loc = glGetUniformLocation(this.program, sampler.getIdentifier());
			
			if (loc == -1) {
				LOGGER.warning("Could not find sampler '" + sampler.getIdentifier() + "' in the program '" + this.identifier + "'.");
				return true;
			} else {
				sampler.setActiveTexture(activeTexture.getAndIncrement());
				return false;
			}
			
		});
		
		this.uniforms.values().removeIf(uniform -> {
			
			int loc = glGetUniformLocation(this.program, uniform.identifier);
			
			if (loc == -1) {
				LOGGER.warning("Could not find uniform '" + uniform.identifier + "' in the program '" + this.identifier + "'.");
				return true;
			} else {
				uniform.location = loc;
				uniform.init();
				return false;
			}
			
		});
		
		/*
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
		*/
		
	}
	
	/**
	 * Internal method to setup attributes and get their GL location.
	 */
	private void setupAttributes() {
		
		AtomicInteger index = new AtomicInteger(0);
		
		this.attributes.values().removeIf(attribute -> {
			
			int loc = glGetAttribLocation(this.program, attribute.getIdentifier());
			
			if (loc == -1) {
				LOGGER.warning("Could not find attribute '" + attribute.getIdentifier() + "' in the program '" + this.identifier + "'.");
				return true;
			} else {
				attribute.setIndex(index.getAndIncrement());
				return false;
			}
			
		});
		
		/*
		for ( ShaderAttribute attribute : this.attributes ) {
			
			int location = glGetAttribLocation( this.program, attribute.identifier );
			
			if ( location == -1 ) {
				
				LOGGER.warning( "Could not find attribute '" + attribute.identifier + "' in the program '" + this.identifier + "'." );
				
			} else {
				
				attribute.location = location;
				
			}
			
		}*/
		
	}
	
	/**
	 * <u><b>WORK IN PROGRESS</b></u><br>
	 * Setup uniform blocks.
	 */
	private void setupUniformBlocks() {
		
		this.uniformBlocks.values().removeIf(block -> {
			
			if (!block.usable()) {
				LOGGER.warning("Could not use uniform block '" + block.identifier + "' in the program '" + this.identifier + "', initialize it first.");
				return true;
			}
			
			int index = glGetUniformBlockIndex(this.program, block.identifier);
			
			if (index == -1) {
				LOGGER.warning("Could not find uniform block '" + block.identifier + "' in the program '" + this.identifier + "'.");
				return true;
			} else {
				glUniformBlockBinding(this.program, index, block.binding);
				return false;
			}
			
		});
		
		/*
		for ( ShaderUniformBlock block : this.uniformBlocks.values() ) {
			
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
		*/
		
	}
	
	/**
	 * Set this manager's program as current to GL, used to draw on the bound framebuffer.
	 */
	public void use() {
		
		this.checkNotBuilt();
		
		currentShaderManager = this;
		
		if ( this.program != currentProgram ) {
			
			glUseProgram( this.program );
			currentProgram = this.program;
			
		}
		
		for (ShaderSampler sampler : this.samplers.values()) {
			
			glUniform1i(sampler.getLocation(), sampler.getActiveTexture());
			
			if (sampler.hasSamplerObject()) {
				sampler.bind();
			}
			
		}
		
		/*for ( int i = 0; i < this.samplers.size(); i++ ) {
			
			ShaderSampler sampler = this.samplers.get( i );
			
			if ( sampler.object != null ) {
				
				TextureObject.bindTexture( sampler.object.getSamplerId(), i );
				glUniform1i( sampler.location, i );
				
			}
			
		}*/
		
		for (ShaderUniform uniform : this.uniforms.values()) {
			if (uniform.usable()) {
				uniform.upload();
			}
		}
		
	}
	
	/**
	 * @return True if the current used (see {@link #use()}) shader manager is this instance.
	 */
	public boolean isCurrent() {
		return currentShaderManager == this;
	}
	
	/**
	 * Finish use of the shader program to GL.
	 */
	public void end() {
		
		this.checkNotBuilt();
		
		glUseProgram( 0 );
		currentProgram = -1;
		currentShaderManager = null;
		
		for (ShaderSampler sampler : this.samplers.values()) {
			if (sampler.hasSamplerObject()) {
				sampler.bind();
			}
		}
		
		/*for ( int i = 0; i < this.samplers.size(); i++ )
			if ( this.samplers.get( i ).object != null )
				TextureObject.unbind( i );*/
		
	}
	
	/**
	 * Delete the shader program to GL and reset using {@link #reset()}.
	 */
	public void delete() {
		
		if ( !this.built ) return;
		
		this.vertexShaderLoader.releaseShader();
		this.fragmentShaderLoader.releaseShader();
		
		glDeleteProgram( this.program );
		
		for (ShaderUniform uniform : this.uniforms.values()) {
			if (uniform.usable()) {
				uniform.delete();
			}
		}
		
		this.reset();
		
	}
	
	@Override
	protected void finalize() {
		this.delete();
	}
	
	@Override
	public ShaderUniformBase getShaderUniform(String identifier) {
		return this.uniforms.get(identifier);
	}
	
	/**
	 * Set a sampler object to be passed as 'sampler2D' uniform type, registered previously.<br>
	 * If the sampler is currently used (see {@link #use()}), it directly use it as new sampler for the shader program.
	 * @param identifier The sampler identifier, previously register using {@link #registerSampler(String)}.
	 * @param object The object to use as sampler.
	 */
	public void setSamplerObject(String identifier, ShaderSamplerObject object) {
		
		ShaderSampler sampler = this.samplers.get(identifier);
		
		if (sampler == null) {
			throw new IllegalArgumentException("Can't find sampler named '" + identifier + "' in shader manager '" + this.identifier + "'");
		} else {
		
			sampler.setSamplerObject(object);
			
			if (this.isCurrent()) {
				sampler.bind();
			}
		
		}
		
		/*for ( int i = 0; i < this.samplers.size(); i++ ) {
			
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
		
		throw new IllegalArgumentException( "Can't find sampler named '" + identifier + "' in shader manager '" + this.identifier + "'" );*/
		
	}
	
	/**
	 * Get a shader attribute object, created by {@link #registerAttribute(VertexElement)}.
	 * @param identifier The attribute identifier.
	 * @return The shader attribute object, or Null if no shader attribute associated with this identifier.
	 */
	public ShaderAttribute getShaderAttribute(String identifier) {
		return this.attributes.get(identifier);
	}
	
	/**
	 * Get a shader attribute object, create by {@link #registerAttribute(VertexElement)}.
	 * @param vertexElement The attribute vertex element definition.
	 * @return The shader attribute object, or Null if no shader attribute associated with this vertex element definition.
	 */
	public ShaderAttribute getShaderAttribute(VertexElement vertexElement) {
		return this.attributesByElts.get(vertexElement);
	}
	
	/**
	 * Get a shader attribute object, create bu {@link #registerAttribute(VertexElement)}
	 * from its built location, error thrown if shader not built.
	 * @param location The known attribute location.
	 * @return The shader attribute if found at this location, else Null.
	 * @throws IllegalStateException If the shader isn't built.
	 */
	@Deprecated
	public ShaderAttribute getShaderAttribute(int location) {
		
		this.checkNotBuilt();
		
		for (ShaderAttribute attr : this.attributes.values())
			if (attr.getLocation() == location)
				return attr;
			
		return null;
		
	}
	
	/**
	 * Get a shader attribute index in internal registration list.
	 * @param identifier The attribute identifier.
	 * @return Attribute index, or -1 if not found.
	 */
	public int getShaderAttributeIndex(String identifier) {
		ShaderAttribute attr = this.attributes.get(identifier);
		return attr == null ? -1 : attr.getIndex();
	}
	
	/*
	 * Get a shader attribute index in internal registration list.
	 * @param attribute The attribute instance, can be queried by
	 * {@link #getShaderAttribute(String)} or {@link #getShaderAttribute(VertexElement)}.
	 * @return Attribute index, or -1 if not found.
	 */
	/*public int getShaderAttributeIndex(ShaderAttribute attribute) {
		return this.attributes.indexOf( attribute );
	}*/
	
	/**
	 * Get a shader attribute location (in GL program) from its vertex element definition.
	 * @param vertexElement The vertex element definition of the attribute.
	 * @return Attribute GL program location, or -1 if not found.
	 */
	public int getShaderAttributeLocation(VertexElement vertexElement) {
		ShaderAttribute attr = this.attributesByElts.get(vertexElement);
		return attr == null ? -1 : attr.getLocation();
	}
	
	/**
	 * Get a shader attribute location (in GL program) from its identifier.
	 * @param identifier The attribute identifier.
	 * @return Attribute GL program location, or -1 if not found.
	 */
	public int getShaderAttributeLocation(String identifier) {
		ShaderAttribute attr = this.attributes.get(identifier);
		return attr == null ? -1 : attr.getLocation();
	}
	
	/**
	 * Get a shader attribute location (in GL program), and use a consumer to return it.
	 * @param vertexElement The vertex element definition of the attribute.
	 * @param locationConsumer The consumer to execute if the attribute was found.
	 */
	public void getShaderAttributeLocationSafe(VertexElement vertexElement, Consumer<Integer> locationConsumer) {
		int location = this.getShaderAttributeLocation(vertexElement);
		if (location != -1) locationConsumer.accept(location);
	}
	
	/**
	 * @return An immutable list of current shader attributes.
	 */
	public Collection<ShaderAttribute> getShaderAttributes() {
		return this.attributes.values();
	}
	
	/**
	 * @return Shader attribute count.
	 */
	public int getShaderAttributeCount() {
		return this.attributes.size();
	}
	
	/**
	 * <u><b>WORK IN PROGRESS</b></u><br>
	 * Get an uniform block from its identifier.
	 * @param identifier The uniform block identifier.
	 * @return Found uniform block, or Null if not found.
	 */
	public ShaderUniformBlock getUniformBlock(String identifier) {
		return this.uniformBlocks.get(identifier);
	}
	
	/**
	 * @return GL program identifier.
	 */
	public int getProgram() {
		return this.program;
	}
	
	/**
	 * @return Vertex {@link ShaderLoader}
	 */
	public ShaderLoader getVertexShaderLoader() {
		return this.vertexShaderLoader;
	}
	
	/**
	 * @return Fragment {@link ShaderLoader}
	 */
	public ShaderLoader getFragmentShaderLoader() {
		return this.fragmentShaderLoader;
	}
	
}
