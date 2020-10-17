package io.msengine.client.graphics.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class BufferArray implements AutoCloseable {
	
	public static <A extends BufferArray> Builder<A> newBuilder(BufferArrayCreator<A> creator) {
		return new Builder<>(creator);
	}
	
	public static Builder<BufferArray> newBuilder() {
		return newBuilder(BufferArray::new);
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
	
	@Override
	public void close() {
		
		if (this.vao != 0) {
			glDeleteVertexArrays(this.vao);
			this.vao = 0;
		}
		
		for (int i = 0, vbo; i < this.vbos.length; ++i) {
			if ((vbo = this.vbos[i]) != 0) {
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
		
		private Builder(BufferArrayCreator<A> creator) {
			this.creator = creator;
		}
		
		public BufferBuilder<A> newBuffer() {
			BufferBuilder<A> builder = new BufferBuilder<>(this);
			this.builders.add(builder);
			return builder;
		}
		
		public A build() {
			
			int vao = glGenVertexArrays();
			glBindVertexArray(vao);
			
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
		
		public BufferBuilder<A> withAttrib(int attribLocation, int dataType, int dataTypeSizeOf, int dataCount) {
			
			int sizeOf = dataCount * dataTypeSizeOf;
			
			this.attribs.add(new BufferAttrib(attribLocation, dataType, dataCount, sizeOf));
			this.totalSize += sizeOf;
			return this;
			
		}
		
		public BufferBuilder<A> withAttrib(int attribLocation, int dataType, int dataCount) {
			return this.withAttrib(attribLocation, dataType, getDataTypeSizeOf(dataType), dataCount);
		}
		
		public Builder<A> build() {
			return this.builder;
		}
		
		private static int getDataTypeSizeOf(int dataType) {
			switch (dataType) {
				case GL_BYTE:
				case GL_UNSIGNED_BYTE:
					return 1;
				case GL_SHORT:
				case GL_UNSIGNED_SHORT:
					return 2;
				case GL_INT:
				case GL_UNSIGNED_INT:
					return 4;
				default:
					throw new IllegalArgumentException("The given data type sizeof is not internally known, please use long withAttrib() method.");
			}
		}
		
	}
	
	private static class BufferAttrib {
	
		private final int attribLocation;
		private final int dataType;
		private final int dataCount;
		private final int sizeOf;
		
		public BufferAttrib(int attribLocation, int dataType, int dataCount, int sizeOf) {
			this.attribLocation = attribLocation;
			this.dataType = dataType;
			this.dataCount = dataCount;
			this.sizeOf = sizeOf;
		}
	
	}
	
}
