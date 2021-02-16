package io.msengine.client.graphics.buffer;

import io.msengine.client.graphics.util.DataType;
import io.msengine.client.renderer.util.GLUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glIsVertexArray;

public abstract class BufferArray implements AutoCloseable {
	
	// Constants \\
	
	/**
	 * Default primitive type used for drawing. Currently {@link GL11#GL_TRIANGLES}.
	 */
	public static final int DEFAULT_PRIMITIVE_TYPE = GL_TRIANGLES;
	
	// Builders //
	
	public static <A extends BufferArray> Builder<A> newBuilder(BufferArrayCreator<A> creator) {
		return new Builder<>(creator);
	}
	
	public static Builder<IndexedBufferArray> newIndexedBuilder() {
		return newBuilder(IndexedBufferArray::new);
	}
	
	public static Builder<VerticesBufferArray> newVerticesBuilder() {
		return newBuilder(VerticesBufferArray::new);
	}
	
	// Class //
	
	private int vao;
	private final int[] vbos;
	
	public BufferArray(int vao, int[] vbos) {
		this.vao = vao;
		this.vbos = vbos;
	}
	
	public int getVao() {
		return this.vao;
	}
	
	public int getVbo(int index) {
		return this.vbos[index];
	}
	
	/**
	 * Bind the Vertex Array Object (VAO).
	 */
	public void bindVao() {
		glBindVertexArray(this.vao);
	}
	
	/**
	 * Bind the Vertex Buffer Object (VBO) at a specific index
	 * (index is set by the builder).
	 * @param index Buffer index at build.
	 * @param target OpenGL buffer target.
	 */
	public void bindVbo(int index, int target) {
		glBindBuffer(target, this.vbos[index]);
	}
	
	/**
	 * Unbind any previously bound VAO.
	 * @see #bindVao()
	 */
	public static void unbindVao() {
		glBindVertexArray(0);
	}
	
	/**
	 * Unbind the VBO previously bound to a specific target.
	 * @param target OpenGL buffer target.
	 */
	public static void unbindVbo(int target) {
		glBindBuffer(target, 0);
	}
	
	/**
	 * Allocate data in specified VBO for future call
	 * to {@link #uploadVboSubData(int, long, FloatBuffer)}
	 * or {@link #uploadVboSubData(int, long, IntBuffer)}.
	 * @param index Buffer index at build.
	 * @param size Number of bytes to allocate.
	 * @param usage Buffer usage.
	 */
	public void allocateVboData(int index, long size, BufferUsage usage) {
		this.bindVbo(index, GL_ARRAY_BUFFER);
		glBufferData(GL_ARRAY_BUFFER, size, usage.value);
	}
	
	/**
	 * Upload float data to specified VBO.
	 * @param index Buffer index at build.
	 * @param data Data to upload.
	 * @param usage Buffer usage.
	 */
	public void uploadVboData(int index, FloatBuffer data, BufferUsage usage) {
		this.bindVbo(index, GL_ARRAY_BUFFER);
		glBufferData(GL_ARRAY_BUFFER, data, usage.value);
	}
	
	/**
	 * Upload integer data to specified VBO.
	 * @param index Buffer index at build.
	 * @param data Data to upload.
	 * @param usage Buffer usage.
	 */
	public void uploadVboData(int index, IntBuffer data, BufferUsage usage) {
		this.bindVbo(index, GL_ARRAY_BUFFER);
		glBufferData(GL_ARRAY_BUFFER, data, usage.value);
	}
	
	/**
	 * Upload float data to specified VBO in allocated space given
	 * by {@link #allocateVboData(int, long, BufferUsage)}.
	 * @param index Buffer index at build.
	 * @param offset Number of bytes to offset in allocated space
	 * @param data {@link FloatBuffer} data to upload
	 */
	public void uploadVboSubData(int index, long offset, FloatBuffer data) {
		this.bindVbo(index, GL_ARRAY_BUFFER);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}
	
	/**
	 * Upload integer data to specified VBO in allocated space given
	 * by {@link #allocateVboData(int, long, BufferUsage)}.
	 * @param index Buffer index at build.
	 * @param offset Number of bytes to offset in allocated space.
	 * @param data {@link IntBuffer} data to upload.
	 */
	public void uploadVboSubData(int index, long offset, IntBuffer data) {
		this.bindVbo(index, GL_ARRAY_BUFFER);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}
	
	/**
	 * Draw buffer elements.
	 * @param primitiveType OpenGL primitive type.
	 */
	public abstract void draw(int primitiveType);
	
	/**
	 * Draw buffer elements in specified range, this range is not checked
	 * against interne buffer indices or vertices count.
	 * @param primitiveType OpenGL primitive type.
	 * @param offset The first element (indices or vertices) to render.
	 * @param count The number of elements to render.
	 */
	public abstract void draw(int primitiveType, int offset, int count);
	
	/**
	 * Draw buffer elements using the default primitive type (triangle).
	 * @see #draw(int)
	 * @see #DEFAULT_PRIMITIVE_TYPE
	 */
	public void draw() {
		this.draw(DEFAULT_PRIMITIVE_TYPE);
	}
	
	/**
	 * Draw buffer elements in specified range, this range is not checked
	 * against interne buffer indices or vertices count. This method
	 * use the default primitive type (triangle).
	 * @param offset The first element (indices or vertices) to render.
	 * @param count The number of elements to render.
	 * @see #draw(int, int, int)
	 * @see #DEFAULT_PRIMITIVE_TYPE
	 */
	public void draw(int offset, int count) {
		this.draw(DEFAULT_PRIMITIVE_TYPE, offset, count);
	}
	
	@Override
	public void close() {
		
		if (glIsVertexArray(this.vao)) {
			glDeleteVertexArrays(this.vao);
			this.vao = 0;
		}
		
		for (int i = 0, vbo; i < this.vbos.length; ++i) {
			if (glIsBuffer(vbo = this.vbos[i])) {
				glDeleteBuffers(vbo);
				this.vbos[i] = 0;
			}
		}
		
	}
	
	// Builder pattern //
	
	@FunctionalInterface
	public interface BufferArrayCreator<A extends BufferArray> {
		A create(int vao, int[] vbos);
	}
	
	public static class Builder<A extends BufferArray> {
		
		private final List<BufferBuilder<A>> builders = new ArrayList<>();
		private final BufferArrayCreator<A> creator;
		private final List<BufferEnableAttrib> vertexAttribs = new ArrayList<>();
		
		private Builder(BufferArrayCreator<A> creator) {
			this.creator = creator;
		}
		
		public Builder<A> withCond(boolean cond, Consumer<Builder<A>> ifTrue) {
			if (cond) {
				ifTrue.accept(this);
			}
			return this;
		}
		
		public BufferBuilder<A> newBuffer() {
			BufferBuilder<A> builder = new BufferBuilder<>(this);
			this.builders.add(builder);
			return builder;
		}
		
		public Builder<A> withVertexAttrib(int location, boolean enabled) {
			this.vertexAttribs.add(new BufferEnableAttrib(location, enabled));
			return this;
		}
		
		public A build() {
			
			int vao = glGenVertexArrays();
			glBindVertexArray(vao);
			
			for (BufferEnableAttrib vertexAttrib : this.vertexAttribs) {
				GLUtils.glSetVertexAttribArray(vertexAttrib.attribLocation, vertexAttrib.enabled);
			}
			
			int count = this.builders.size();
			int[] vbos = new int[count];
			BufferBuilder<A> buf;
			
			for (int i = 0; i < count; ++i) {
				
				buf = this.builders.get(i);
				
				int vbo = glGenBuffers();
				glBindBuffer(GL_ARRAY_BUFFER, vbo);
				vbos[i] = vbo;
				
				int offset = 0;
				
				for (BufferAttrib attrib : buf.attribs) {
					glVertexAttribPointer(attrib.attribLocation, attrib.dataCount, attrib.dataType, false, buf.totalSize, offset);
					offset += attrib.sizeOf;
				}
				
			}
			
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
			
			return this.creator.create(vao, vbos);
			
		}
		
	}
	
	public static class BufferBuilder<A extends BufferArray> {
		
		private final Builder<A> builder;
		private final List<BufferAttrib> attribs = new ArrayList<>();
		private int totalSize;
		
		private BufferBuilder(Builder<A> builder) {
			this.builder = builder;
		}
		
		public BufferBuilder<A> withCond(boolean cond, Consumer<BufferBuilder<A>> ifTrue) {
			if (cond) {
				ifTrue.accept(this);
			}
			return this;
		}
		
		public BufferBuilder<A> withAttrib(int attribLocation, DataType dataType, int dataCount) {
			int sizeOf = dataCount * dataType.size;
			this.attribs.add(new BufferAttrib(attribLocation, dataType.value, dataCount, sizeOf));
			this.totalSize += sizeOf;
			return this;
		}
		
		public Builder<A> build() {
			return this.builder;
		}
		
	}
	
	private static class BufferAttrib {
	
		private final int attribLocation;
		private final int dataType;
		private final int dataCount;
		private final int sizeOf;
		
		private BufferAttrib(int attribLocation, int dataType, int dataCount, int sizeOf) {
			this.attribLocation = attribLocation;
			this.dataType = dataType;
			this.dataCount = dataCount;
			this.sizeOf = sizeOf;
		}
	
	}
	
	private static class BufferEnableAttrib {
		
		private final int attribLocation;
		private final boolean enabled;
		
		private BufferEnableAttrib(int attribLocation, boolean enabled) {
			this.attribLocation = attribLocation;
			this.enabled = enabled;
		}
		
	}
	
}
