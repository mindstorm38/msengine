package io.msengine.client.renderer.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;
import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * <p>Used to extends defaults OpenGL functions</p>
 *
 * <p><b>This class might be partially moved in {@link io.msengine.client.graphics} in the future, or simply deprecated.</b></p>
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class GLUtils {
	
	/**
	 * Shortcut for glEnable(target) (enabled == true) and glDisable(target) (enabled == false)
	 * @param target GL parameter target
	 * @param enabled Enable (glEnable) or disable (glDisable)
	 */
	public static void glSet(int target, boolean enabled) {
		if ( enabled ) glEnable(target);
		else glDisable( target );
	}
	
	/**
	 * Shortcut for glEnableVertexAttribArray(location) (enabled == true) and glDisableVertexAttribArray( location ) (enabled == false)
	 * @param location GL vertex attrib location
	 * @param enabled Enable (glEnableVertexAttribArray) or disable (glDisableVertexAttribArray)
	 */
	public static void glSetVertexAttribArray(int location, boolean enabled) {
		if ( enabled ) glEnableVertexAttribArray( location );
		else glDisableVertexAttribArray( location );
	}
	
	/**
	 * Check current GL error
	 * @param message Message to send before error description
	 */
	public static void checkGLError(String message) {
		
		int i = glGetError();
		
		if ( i != GL_NO_ERROR ) {
			
			String errorString = null;
			
			switch ( i ) {
				case GL_INVALID_ENUM:
					errorString = "INVALID_ENUM : An unacceptable value is specified for an enumerated argument. The offending command is ignored and has no other side effect than to set the error flag.";
					break;
				case GL_INVALID_VALUE:
					errorString = "INVALID_VALUE : A numeric argument is out of range. The offending command is ignored and has no other side effect than to set the error flag.";
					break;
				case GL_INVALID_OPERATION:
					errorString = "INVALID_OPERATION : The specified operation is not allowed in the current state. The offending command is ignored and has no other side effect than to set the error flag.";
					break;
				case GL_INVALID_FRAMEBUFFER_OPERATION:
					errorString = "INVALID_FRAMEBUFFER_OPERATION : The framebuffer object is not complete. The offending command is ignored and has no other side effect than to set the error flag.";
					break;
				case GL_OUT_OF_MEMORY:
					errorString = "OUT_OF_MEMORY : There is not enough memory left to execute the command. The state of the GL is undefined, except for the state of the error flags, after this error is recorded.";
					break;
				case GL_STACK_UNDERFLOW:
					errorString = "STACK_UNDERFLOW : An attempt has been made to perform an operation that would cause an internal stack to underflow.";
					break;
				case GL_STACK_OVERFLOW:
					errorString = "STACK_OVERFLOW : An attempt has been made to perform an operation that would cause an internal stack to overflow.";
					break;
				default:
					errorString = "UNKNOWN_ERROR : " + i;
					break;
			}
			
			LOGGER.severe( "GL Error has occured :" );
			LOGGER.severe( "- " + message );
			LOGGER.severe( errorString );
					
		}
		
	}
	
}
