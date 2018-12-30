package io.msengine.client.renderer.shader;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Function;

import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.system.MemoryUtil;

public enum ShaderValueType {
	
	INT		( MemoryUtil::memAllocInt, 1 ) {
		
		@Override
		public void upload(int location, Buffer buffer) {
			glUniform1iv( location, (IntBuffer) buffer );
		}
		
	},
	IVEC2	( MemoryUtil::memAllocInt, 2 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniform2iv( location, (IntBuffer) buffer );
		}
		
	},
	IVEC3	( MemoryUtil::memAllocInt, 3 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniform3iv( location, (IntBuffer) buffer );
		}
		
	},
	IVEC4	( MemoryUtil::memAllocInt, 4 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniform4iv( location, (IntBuffer) buffer );
		}
		
	},
	FLOAT	( MemoryUtil::memAllocFloat, 1 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniform1fv( location, (FloatBuffer) buffer );
		}
		
	},
	VEC2	( MemoryUtil::memAllocFloat, 2 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniform2fv( location, (FloatBuffer) buffer );
		}
		
	},
	VEC3	( MemoryUtil::memAllocFloat, 3 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniform3fv( location, (FloatBuffer) buffer );
		}
		
	},
	VEC4	( MemoryUtil::memAllocFloat, 4 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniform4fv( location, (FloatBuffer) buffer );
		}
		
	},
	MAT3	( MemoryUtil::memAllocFloat, 3 * 3 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniformMatrix3fv( location, false, (FloatBuffer) buffer );
		}
		
	},
	MAT4	( MemoryUtil::memAllocFloat, 4 * 4 ) {
		
		public void upload(int location, Buffer buffer) {
			glUniformMatrix4fv( location, false, (FloatBuffer) buffer );
		}
		
	};
	
	public final Function<Integer, Buffer> createBufferFunction;
	public final int size;
	public final int uboSize;
	
	private ShaderValueType(Function<Integer, Buffer> createBufferFunction, int size, int uboSize) {
		
		this.createBufferFunction = createBufferFunction;
		this.size = size;
		this.uboSize = uboSize;
		
	}
	
	private ShaderValueType(Function<Integer, Buffer> createBufferFunction, int size) {
		this( createBufferFunction, size, size );
	}
	
	public abstract void upload(int location, Buffer buffer);
	
}
