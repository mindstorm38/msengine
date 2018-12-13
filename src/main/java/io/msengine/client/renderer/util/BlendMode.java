package io.msengine.client.renderer.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

public abstract class BlendMode {
	
	public static final BlendMode TRANSPARENCY = new BlendMode() {
		
		public void use() {
			
			glBlendEquation( GL_FUNC_ADD );
			//glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
			glBlendFuncSeparate( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE );
			
		}
		
	};
	
	public static final BlendMode MULT = new BlendMode() {
		
		public void use() {
			
			glBlendEquation( GL_FUNC_ADD );
			glBlendFuncSeparate( GL_ZERO, GL_SRC_COLOR, GL_ZERO, GL_SRC_ALPHA );
			
		}
		
	};
	
	public static final BlendMode MULT_COLOR = new BlendMode() {
		
		public void use() {
			
			glBlendEquation( GL_FUNC_ADD );
			glBlendFuncSeparate( GL_ZERO, GL_SRC_COLOR, GL_ZERO, GL_ONE );
			
		}
		
	};
	
	public static final BlendMode SET = new BlendMode() {
		
		public void use() {
			
			glBlendEquation( GL_FUNC_ADD );
			glBlendFuncSeparate( GL_ZERO, GL_ONE, GL_ZERO, GL_ONE );
			
		}
		
	};
	
	public abstract void use();
	
}
