package io.msengine.client.graphics.util;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class Blending {

	public static void enable(boolean enable) {
		if (enable) glEnable(GL_BLEND);
		else glDisable(GL_BLEND);
	}
	
	public static void enable() {
		glEnable(GL_BLEND);
	}
	
	public static void disable() {
		glDisable(GL_BLEND);
	}
	
	public static void transparency() {
		glBlendEquation(GL_FUNC_ADD);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);
	}
	
	public static void multiplied() {
		glBlendEquation(GL_FUNC_ADD);
		glBlendFuncSeparate(GL_ZERO, GL_SRC_COLOR, GL_ZERO, GL_SRC_ALPHA);
	}
	
	public static void colorMultiplied() {
		glBlendEquation(GL_FUNC_ADD);
		glBlendFuncSeparate(GL_ZERO, GL_SRC_COLOR, GL_ZERO, GL_ONE);
	}
	
	public static void set() {
		glBlendEquation(GL_FUNC_ADD);
		glBlendFuncSeparate(GL_ZERO, GL_ONE, GL_ZERO, GL_ONE);
	}

}
