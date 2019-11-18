package io.msengine.client.renderer.vertex;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static io.msengine.client.renderer.util.GLUtils.glSetVertexAttribArray;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import io.msengine.client.renderer.shader.ShaderAttribute;
import io.msengine.client.renderer.shader.ShaderManager;
import io.msengine.client.renderer.util.BufferUsage;
import io.sutil.CollectionUtils;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 *
 * Draw buffers are an high level abstraction way to upload java buffers that contains vertices data to OpenGL context.<br>
 * They are bound at instantiation to a {@link ShaderManager} for linking with {@link VertexArrayFormat}
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class DrawBuffer {
	
	// Constants \\
	
	/**
	 * Default primitive type used for drawing. Currently {@link GL11#GL_TRIANGLES}.
	 */
	public static final int DEFAULT_PRIMITIVE_TYPE = GL_TRIANGLES;
	
	// Static \\
	
	protected static int currentVAOLocation = 0;
	
	protected static DrawBuffer currentVBODrawBuffer = null;
	protected static int currentVBOIndex = -1;
	protected static int currentVBOLocation = 0;
	
	// Class \\
	
	protected final ShaderManager shaderManager;
	protected final VertexArrayFormat format;
	
	protected final int[] attributesLocations;
	protected final boolean[] attributesStates;
	
	protected final int vao;
	
	protected final int[] vbos;
	protected final String[] vbosIdentifiers;
	
	protected boolean deleted;
	
	/**
	 * Construct a new {@link DrawBuffer} with specified {@link ShaderManager}, {@link VertexArrayFormat} and enabled vertex attributes
	 * @param shaderManager {@link ShaderManager} used for link with format
	 * @param format {@link VertexArrayFormat} used for link with <code>ShaderManager</code>
	 * @param enabledVertexAttribsIdentifiers Array of all enabled vertex attributes identifiers
	 */
	public DrawBuffer(ShaderManager shaderManager, VertexArrayFormat format, String...enabledVertexAttribsIdentifiers) {
		
		if ( shaderManager == null ) throw new NullPointerException( "Shader manager must not be null" );
		if ( !shaderManager.wasBuilt() ) throw new IllegalArgumentException( "Shader manager must be built" );
		if ( format == null ) throw new NullPointerException( "Format must not be null" );
		
		this.shaderManager = shaderManager;
		this.format = format;
		
		// Attributes locations and states
		
		final List<ShaderAttribute> attributes = shaderManager.getShaderAttributes();
		
		this.attributesLocations = new int[ attributes.size() ];
		this.attributesStates = new boolean[ attributes.size() ];
		
		// Creating Vertex Arrays Objects and Vertex Buffer Object list.
		
		this.vao = glGenVertexArrays();
		
		this.vbos = new int[ format.buffers.length ];
		Arrays.fill( this.vbos, -1 );
		
		this.vbosIdentifiers = new String[ this.vbos.length ];
		for ( int i = 0; i < format.buffers.length; i++ )
			this.vbosIdentifiers[ i ] = format.buffers[ i ].identifier;
		
		this.deleted = false;
		
		// Generating buffers and attributes states
		
		ShaderAttribute attribute;
		VertexElement element;
		VertexBufferFormat bufferFormat;
		int bufferIndex, attributeLocation;
		
		this.bindVao();
		
		for ( int i = 0; i < attributes.size(); i++ ) {
			
			attribute = attributes.get( i );
			
			attributeLocation = this.attributesLocations[ i ] = attribute.getLocation();
			
			if ( attributeLocation == -1 ) {
				
				LOGGER.warning("Invalid attribute location for '" + attribute.getIdentifier() + "', can't be enabled in the Draw Buffer. If this is the only attribute in a VBO, the VBO is not created and errors could occur." );
				
			} else {
				
				if ( this.attributesStates[ i ] = CollectionUtils.arrayContains( enabledVertexAttribsIdentifiers, attribute.getIdentifier() ) ) {
					
					// If the attribute is enbled, get all informations about it.
					element = attribute.getVertexElement();
					bufferFormat = format.getBufferForElement( element );
					bufferIndex = format.getBufferIndex( bufferFormat );
					
					// If this attribute's vertex element's buffer is not initialized, generate a GL buffer.
					if ( this.vbos[ bufferIndex ] == -1 )
						this.vbos[ bufferIndex ] = glGenBuffers();
					
					// Bind a VBO at a specific index.
					this.bindVbo( bufferIndex );
					
					// Specifiy the position and type of a vertex attribute in the VBO.
					// If the buffer has more than one vertex element definition, it define offsets in the buffer.
					// For example, if a buffer has 2 elements (in this order) : position (2f), color (4f); offsets are :
					// - position (offset: 0, stride: 24)
					// - color (offset: 8, stride: 24)
					// Example buffer : [<xy0><rgba0><xy1><rgba1>...<xy(n)><rgba(n)>]
					glVertexAttribPointer( attributeLocation, element.count, element.type.i, false, bufferFormat.size, bufferFormat.getElementOffset( element ) );
					
				}
				
			}
			
		}
		
		// To finish unbind last VBO, and the VAO.
		unbindVbo();
		unbindVao();
		
	}
	
	// - Infos
	
	/**
	 * @return Linked {@link ShaderManager}
	 */
	public ShaderManager getShaderManager() {
		return this.shaderManager;
	}
	
	/**
	 * @return Linked {@link VertexArrayFormat}
	 */
	public VertexArrayFormat getFormat() {
		return this.format;
	}
	
	// - Shader Manager link
	
	/**
	 * @throws IllegalStateException If the shader manager linked to this buffer is not active.
	 */
	protected void checkCurrentShaderManager() {
		if ( !this.shaderManager.isCurrent() ) throw new IllegalStateException("The linked ShaderManager must be used to use it");
	}
	
	// - Deleted
	
	/**
	 * @throws IllegalStateException If this buffer was deleted.
	 */
	protected void checkDeleted() {
		if ( this.deleted ) throw new IllegalStateException("Unusable because it has been deleted.");
	}
	
	/**
	 * Delete this draw buffer
	 */
	public void delete() {
		
		this.checkDeleted();
		
		this.deleted = true;
		
		glDeleteVertexArrays( this.vao );
		for ( int vbo : this.vbos ) glDeleteBuffers( vbo );
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		if ( !this.deleted ) this.delete();
	}
	
	// - Vertex Attributes
	
	/**
	 * Get if a vertex attribute is enabled. The enabling of attributes is specified in the constructor {@link #DrawBuffer(ShaderManager, VertexArrayFormat, String...)}
	 * @param identifier Vertex attribute identifier
	 * @return Is vertex attribute enabled
	 */
	public boolean isVertexAttribEnabled(String identifier) {
		int index = this.shaderManager.getShaderAttributeIndex( identifier );
		return index == -1 || this.attributesStates[ index ];
	}

	/**
	 * Delegating to the linked shader manager {@link ShaderManager#getShaderAttributeLocation(String)}
	 * @param identifier Vertex Attribute identifier
	 * @return Vertex Attribute location, or -1 if not linked
	 */
	public int getVertexAttribLocation(String identifier) {
		return this.shaderManager.getShaderAttributeLocation( identifier );
	}
	
	// - Vertex Arrays Object
	
	/**
	 * Bind the Vertex Array Object (VAO)
	 */
	public void bindVao() {
		
		this.checkDeleted();
		
		if ( currentVAOLocation != this.vao )
			glBindVertexArray( currentVAOLocation = this.vao );
		
	}
	
	// - Vertex Buffer Object
	
	private void checkVboIndex(int vbo) {
		if ( vbo < 0 || vbo >= this.vbos.length ) throw new IndexOutOfBoundsException( "Invalid Vertex Buffer index" );
	}
	
	/**
	 * Get if a VBO is enabled. A VBO can be disabled if none of these attributes is enabled in the constructor {@link #DrawBuffer(ShaderManager, VertexArrayFormat, String...)}
	 * @param vbo The VBO index
	 * @return Is VBO enabled
	 */
	public boolean isVboEnabled(int vbo) {
		
		this.checkVboIndex( vbo );
		return this.vbos[ vbo ] != -1;
		
	}
	
	/**
	 * Get A VBO index, the index can be used with {@link #bindVbo(int)}
	 * @param identifier Identifier corresponding to water VBO index
	 * @return The VBO Index or -1 if the identifier is invalid
	 */
	public int getVboIndex(String identifier) {
		return CollectionUtils.arrayIndexOf( this.vbosIdentifiers, identifier );
	}
	
	/**
	 * Bind a Vertex Buffer Object (VBO)
	 * @param vbo The VBO index in this buffer (see {@link #getVboIndex(String)})
	 * @throws IllegalArgumentException If this buffer doesn't exists in the {@link #format}
	 * @throws IllegalStateException If the Vertex Buffer is disabled (see {@link #isVboEnabled(int)})
	 */
	public void bindVbo(int vbo) {
		
		this.checkDeleted();
		
		this.checkVboIndex( vbo );
		
		if ( currentVBODrawBuffer == this && currentVBOIndex == vbo ) return;
		
		if ( this.vbos[ vbo ] == -1 )
			throw new IllegalStateException( "Disabled Vertex Buffer" );
		
		glBindBuffer( GL_ARRAY_BUFFER, currentVBOLocation = this.vbos[ vbo ] );
		
		currentVBODrawBuffer = this;
		currentVBOIndex = vbo;
		
	}
	
	/**
	 * Bind a Vertex Buffer Object (VBO) by its name
	 * @param identifier The identifier of the buffer
	 * @throws IllegalArgumentException If this buffer doesn't exists in the {@link #format}
	 * @throws IllegalStateException If the Vertex Buffer is disabled (see {@link #isVboEnabled(int)})
	 */
	public void bindVbo(String identifier) {
		this.bindVbo( this.getVboIndex( identifier ) );
	}
	
	/**
	 * Upload float data to specified VBO
	 * @param identifier Identifier corresponding to the Vertex Buffer Format
	 * @param data {@link FloatBuffer} data to upload
	 * @param usage Buffer usage
	 */
	public void uploadVboData(String identifier, FloatBuffer data, BufferUsage usage) {
		this.bindVbo( identifier );
		glBufferData( GL_ARRAY_BUFFER, data, usage.i );
	}
	
	/**
	 * Upload integer data to specified VBO
	 * @param identifier Identifier corresponding to the Vertex Buffer Format
	 * @param data {@link IntBuffer} data to upload
	 * @param usage Buffer usage
	 */
	public void uploadVboData(String identifier, IntBuffer data, BufferUsage usage) {
		this.bindVbo( identifier );
		glBufferData( GL_ARRAY_BUFFER, data, usage.i );
	}
	
	/**
	 * Allocate data in specified VBO for future call to {@link #uploadVboSubData(String, long, FloatBuffer)} or {@link #uploadVboSubData(String, long, IntBuffer)}
	 * @param identifier Identifier corresponding to the Vertex Buffer Format
	 * @param size Number of bytes to allocate
	 * @param usage Buffer usage
	 */
	public void allocateVboData(String identifier, long size, BufferUsage usage) {
		this.bindVbo( identifier );
		glBufferData( GL_ARRAY_BUFFER, size, usage.i );
	}
	
	/**
	 * Upload float data to specified VBO in allocated space given by {@link #allocateVboData(String, long, BufferUsage)}
	 * @param identifier Identifier corresponding to the Vertex Buffer Format
	 * @param offset Number of bytes to offset in allocated space
	 * @param data {@link FloatBuffer} data to upload
	 */
	public void uploadVboSubData(String identifier, long offset, FloatBuffer data) {
		this.bindVbo( identifier );
		glBufferSubData( GL_ARRAY_BUFFER, offset, data );
	}
	
	/**
	 * Upload integer data to specified VBO in allocated space given by {@link #allocateVboData(String, long, BufferUsage)}
	 * @param identifier Identifier corresponding to the Vertex Buffer Format
	 * @param offset Number of bytes to offset in allocated space
	 * @param data {@link IntBuffer} data to upload
	 */
	public void uploadVboSubData(String identifier, long offset, IntBuffer data) {
		this.bindVbo( identifier );
		glBufferSubData( GL_ARRAY_BUFFER, offset, data );
	}
	
	// - Draw
	
	/**
	 * Bind VAO and enabling/disabling vertex attribute with informations given in constructor {@link #DrawBuffer(ShaderManager, VertexArrayFormat, String...)}
	 */
	protected void preDraw() {
		
		this.checkCurrentShaderManager();
		
		this.bindVao();
		
		int loc;
		
		for ( int i = 0; i < this.attributesLocations.length; i++ ) {
			
			loc = this.attributesLocations[ i ];
			if ( loc == -1 ) continue;
			
			glSetVertexAttribArray( loc, this.attributesStates[ i ] );
			
		}
		
	}
	
	/**
	 * Unbind VAO
	 */
	protected void postDraw() {
		
		unbindVao();
		
	}
	
	// - Static buffer unbinding
	
	/**
	 * Unbind VAO
	 */
	public static void unbindVao() {
		glBindVertexArray( currentVAOLocation = 0 );
	}
	
	/**
	 * Unbind VBO
	 */
	public static void unbindVbo() {
		
		glBindBuffer( GL_ARRAY_BUFFER, currentVBOLocation = 0 );
		
		currentVBODrawBuffer = null;
		currentVBOIndex = -1;
		
	}
	
}
