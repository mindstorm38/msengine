package io.msengine.client.renderer.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;

public class BufferUtils {
	
	public static void safeFree(Buffer buffer) {
		if (buffer != null) MemoryUtil.memFree(buffer);
	}
	
}
