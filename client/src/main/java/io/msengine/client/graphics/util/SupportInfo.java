package io.msengine.client.graphics.util;

import org.lwjgl.opengl.GLCapabilities;

import java.util.function.Predicate;

public class SupportInfo {

	private final Predicate<GLCapabilities> condition;
	
	public SupportInfo(Predicate<GLCapabilities> condition) {
		this.condition = condition;
	}
	
	public boolean isSupported(GLCapabilities caps) {
		return this.condition.test(caps);
	}

}
