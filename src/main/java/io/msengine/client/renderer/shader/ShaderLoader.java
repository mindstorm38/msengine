package io.msengine.client.renderer.shader;

import java.io.IOException;

import io.msengine.common.resource.DetailledResource;
import io.msengine.common.resource.ResourceManager;
import io.sutil.StreamUtils;

import static org.lwjgl.opengl.GL20.*;

public class ShaderLoader {
	
	// Class \\
	
	private final ShaderType type;
	private final String filename;
	private final int shader;
	
	private int useCount;
	
	private ShaderLoader(ShaderType type, String filename, int shader) {
		
		this.type = type;
		this.filename = filename;
		this.shader = shader;
		
		this.useCount = 0;
		
	}
	
	public void attachShader(ShaderManager manager) {
		
		glAttachShader( manager.program, this.shader );
		this.useShader();
		
	}
	
	public void detachShader(ShaderManager manager) {
		
		glDetachShader( manager.program, this.shader );
		
	}
	
	public void useShader() {
		this.useCount++;
	}
	
	public void releaseShader() {
		
		this.useCount--;
		
		if ( this.useCount <= 0 ) {
			
			glDeleteShader( this.shader );
			this.type.shaders.remove( this.filename );
			
		}
		
	}
	
	public static ShaderLoader load(ShaderType type, String filename) throws IOException {
		
		ShaderLoader loader = type.shaders.get( filename );
		
		if ( loader == null ) {
			
			String path = "shaders/" + filename + type.extension;
			DetailledResource resource = ResourceManager.getInstance().getDetailledResource( path );
			
			if ( resource == null ) throw new IOException( "Can't file shader resource at '" + path + "'" );
			
			try {
				
				String content = resource.getText();
				
				int shader = glCreateShader( type.index );
				glShaderSource( shader, content );
				glCompileShader( shader );
				
				if ( glGetShaderi( shader, GL_COMPILE_STATUS ) == 0 )
					throw new ShaderBuildException( glGetShaderInfoLog( shader ) );
				
				loader = new ShaderLoader( type, filename, shader );
				type.shaders.put( filename, loader );
				
			} finally {
				
				StreamUtils.safeclose( resource );
				
			}
			
		}
		
		return loader;
		
	}
	
}
