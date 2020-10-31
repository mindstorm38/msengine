package io.msengine.client.graphics.util;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.function.Predicate;

/**
 * Support class for different thread local GL capabilities.
 */
public class SupportInfo {

	private final ThreadLocal<Boolean> supported;
	
	public SupportInfo(Predicate<GLCapabilities> condition) {
		this.supported = ThreadLocal.withInitial(() -> condition.test(GL.getCapabilities()));
	}
	
	public boolean isSupported() {
		return this.supported.get();
	}

}
