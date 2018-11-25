package io.msengine.client.renderer.util;

import static org.lwjgl.opengl.GL43.*;

import java.util.Map;
import java.util.logging.Level;

import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.system.MemoryUtil;

import io.sutil.CollectionUtils;

import static io.msengine.common.util.GameLogger.LOGGER;

/**
 * 
 * Used to debug OpenGL error
 * 
 * @author Mindstorm38
 *
 */
public class GLDebugMessageCallback implements GLDebugMessageCallbackI {

	private static final Map<Integer, Level> LEVEL_ASSOC = CollectionUtils.createMapInline(
				GL_DEBUG_SEVERITY_NOTIFICATION, Level.INFO,
				GL_DEBUG_SEVERITY_LOW, Level.FINEST,
				GL_DEBUG_SEVERITY_MEDIUM, Level.WARNING,
				GL_DEBUG_SEVERITY_HIGH, Level.SEVERE
			);
	
	@Override
	public void invoke(int rawSource, int rawType, int id, int severity, int length, long rawMessage, long userParam) {
		
		String source = null;
		switch ( rawSource ) {
			case GL_DEBUG_SOURCE_API:
				source = "SOURCE_API";
				break;
			case GL_DEBUG_SOURCE_WINDOW_SYSTEM:
				source = "SOURCE_WINDOW_SYSTEM";
				break;
			case GL_DEBUG_SOURCE_SHADER_COMPILER:
				source = "SOURCE_SHADER_COMPILER";
				break;
			case GL_DEBUG_SOURCE_THIRD_PARTY:
				source = "SOURCE_THIRD_PARTY";
				break;
			case GL_DEBUG_SOURCE_APPLICATION:
				source = "SOURCE_APPLICATION";
				break;
			case GL_DEBUG_SOURCE_OTHER:
				source = "SOURCE_OTHER";
				break;
		}
		
		String type = null;
		switch ( rawType ) {
			case GL_DEBUG_TYPE_ERROR:
				type = "TYPE_ERROR";
				break;
			case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
				type = "TYPE_DEPRECATED_BEHAVIOR";
				break;
			case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
				type = "TYPE_UNDEFINED_BEHAVIOR";
				break;
			case GL_DEBUG_TYPE_PORTABILITY:
				type = "TYPE_PORTABILITY";
				break;
			case GL_DEBUG_TYPE_PERFORMANCE:
				type = "TYPE_PERFORMANCE";
				break;
			case GL_DEBUG_TYPE_MARKER:
				type = "TYPE_MARKER";
				break;
			case GL_DEBUG_TYPE_PUSH_GROUP:
				type = "TYPE_PUSH_GROUP";
				break;
			case GL_DEBUG_TYPE_POP_GROUP:
				type = "TYPE_POP_GROUP";
				break;
			case GL_DEBUG_TYPE_OTHER:
				type = "TYPE_OTHER";
				break;
		}
		
		String message = MemoryUtil.memASCII( rawMessage );
		
		Level level = LEVEL_ASSOC.get( severity );
		
		LOGGER.log( level, "[GL DEBUG] [" + source + ":" + type + "] " + message.replace("%", "%%"), level == Level.SEVERE ? new Throwable() : null );
		
	}

}
