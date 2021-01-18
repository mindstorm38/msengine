package io.msengine.client.renderer.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;

/**
 * Check {@link io.msengine.client.util.BufferAlloc}
 */
@Deprecated
public class BufferUtils {
	
	public static void safeFree(Buffer buffer) {
		if (buffer != null) MemoryUtil.memFree(buffer);
	}
	
}
