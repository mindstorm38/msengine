package io.msengine.client.renderer.shader;

import java.nio.Buffer;
import java.util.function.Function;

import io.msengine.client.renderer.util.DataType;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

/**
 * All value type accepted as uniforms.
 */
@Deprecated
public enum ShaderValueType {
	
	INT   (MemoryUtil::memAllocInt, GL20::glUniform1iv, DataType.INT, 1),
	IVEC2 (MemoryUtil::memAllocInt, GL20::glUniform2iv, DataType.INT, 2),
	IVEC3 (MemoryUtil::memAllocInt, GL20::glUniform3iv, DataType.INT, 3),
	IVEC4 (MemoryUtil::memAllocInt, GL20::glUniform4iv, DataType.INT, 4),
	FLOAT (MemoryUtil::memAllocFloat, GL20::glUniform1fv, DataType.FLOAT, 1),
	VEC2  (MemoryUtil::memAllocFloat, GL20::glUniform2fv, DataType.FLOAT, 2),
	VEC3  (MemoryUtil::memAllocFloat, GL20::glUniform3fv, DataType.FLOAT, 3),
	VEC4  (MemoryUtil::memAllocFloat, GL20::glUniform4fv, DataType.FLOAT, 4),
	MAT3  (MemoryUtil::memAllocFloat, GlUniformUpload.fromMatrix(GL20::glUniformMatrix3fv), DataType.FLOAT, 9),
	MAT4  (MemoryUtil::memAllocFloat, GlUniformUpload.fromMatrix(GL20::glUniformMatrix4fv), DataType.FLOAT, 16);
	
	public final Function<Integer, Buffer> createBufferFunction;
	public final GlUniformUpload<Buffer> glUniformUpload;
	public final DataType type;
	public final int size;
	public final int uboSize;
	
	@SuppressWarnings("unchecked")
	<B extends Buffer> ShaderValueType(Function<Integer, B> createBufferFunction, GlUniformUpload<B> glUniformMethod, DataType type, int size, int uboSize) {
		
		this.createBufferFunction = (Function<Integer, Buffer>) createBufferFunction;
		this.glUniformUpload = (GlUniformUpload<Buffer>) glUniformMethod;
		this.type = type;
		this.size = size;
		this.uboSize = uboSize;
		
	}
	
	<B extends Buffer> ShaderValueType(Function<Integer, B> createBufferFunction, GlUniformUpload<B> glUniformMethod, DataType type, int size) {
		this(createBufferFunction, glUniformMethod, type, size, size);
	}
	
	/**
	 * Upload a uniform to a GL program at a specific location from a buffer.
	 * @param location The location, required
	 * @param buffer The buffer containing the data to be uploaded.
	 */
	public void upload(int location, Buffer buffer) {
		this.glUniformUpload.upload(location, buffer);
	}
	
	@FunctionalInterface
	private interface GlUniformUpload<B extends Buffer> {
		
		void upload(int loc, B buf);
		
		static <T extends Buffer> GlUniformUpload<T> fromMatrix(GlUniformMatrixUpload<T> matrixUpload) {
			return (loc, buf) -> matrixUpload.upload(loc, false, buf);
		}
		
	}
	
	@FunctionalInterface
	private interface GlUniformMatrixUpload<B extends Buffer> {
		void upload(int loc, boolean transpose, B buf);
	}
	
}
