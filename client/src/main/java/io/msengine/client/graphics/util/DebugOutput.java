package io.msengine.client.graphics.util;

import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL43.*;

public class DebugOutput implements GLDebugMessageCallbackI {
	
	private static final Logger LOGGER = Logger.getLogger("gl.debug");
	private static final Map<Integer, Level> LEVEL_ASSOC = new HashMap<>();
	
	public static final SupportInfo SUPPORT = new SupportInfo(cap -> cap.GL_KHR_debug || cap.OpenGL43);
	
	static {
		LEVEL_ASSOC.put(GL_DEBUG_SEVERITY_NOTIFICATION, Level.INFO);
		LEVEL_ASSOC.put(GL_DEBUG_SEVERITY_LOW, Level.FINEST);
		LEVEL_ASSOC.put(GL_DEBUG_SEVERITY_MEDIUM, Level.WARNING);
		LEVEL_ASSOC.put(GL_DEBUG_SEVERITY_HIGH, Level.SEVERE);
	}
	
	public static void registerIfSupported() {
		if (SUPPORT.isSupported()) {
			glEnable(GL_DEBUG_OUTPUT);
			glDebugMessageCallback(new DebugOutput(), 0L);
		}
	}
	
	@Override
	public void invoke(int rawSource, int rawType, int id, int severity, int length, long rawMessage, long userParam) {
		
		String source = null;
		switch ( rawSource ) {
			case GL_DEBUG_SOURCE_API:
				source = "API";
				break;
			case GL_DEBUG_SOURCE_WINDOW_SYSTEM:
				source = "WINDOW_SYSTEM";
				break;
			case GL_DEBUG_SOURCE_SHADER_COMPILER:
				source = "SHADER_COMPILER";
				break;
			case GL_DEBUG_SOURCE_THIRD_PARTY:
				source = "THIRD_PARTY";
				break;
			case GL_DEBUG_SOURCE_APPLICATION:
				source = "APPLICATION";
				break;
			case GL_DEBUG_SOURCE_OTHER:
				source = "OTHER";
				break;
		}
		
		String type = null;
		switch ( rawType ) {
			case GL_DEBUG_TYPE_ERROR:
				type = "ERROR";
				break;
			case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
				type = "DEPRECATED_BEHAVIOR";
				break;
			case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
				type = "UNDEFINED_BEHAVIOR";
				break;
			case GL_DEBUG_TYPE_PORTABILITY:
				type = "PORTABILITY";
				break;
			case GL_DEBUG_TYPE_PERFORMANCE:
				type = "PERFORMANCE";
				break;
			case GL_DEBUG_TYPE_MARKER:
				type = "MARKER";
				break;
			case GL_DEBUG_TYPE_PUSH_GROUP:
				type = "PUSH_GROUP";
				break;
			case GL_DEBUG_TYPE_POP_GROUP:
				type = "POP_GROUP";
				break;
			case GL_DEBUG_TYPE_OTHER:
				type = "OTHER";
				break;
		}
		
		String message = MemoryUtil.memASCII(rawMessage);
		Level level = LEVEL_ASSOC.get(severity);
		LOGGER.log(level, "[" + source + ":" + type + "] " + message.replace("%", "%%"));
		
	}
	
}
