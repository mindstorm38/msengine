package io.msengine.client.renderer.vertex;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import io.msengine.client.renderer.shader.ShaderManager;
import io.msengine.client.renderer.util.BufferUsage;

public class FrameDrawBuffer extends IndicesDrawBuffer {

	public FrameDrawBuffer(ShaderManager shaderManager, VertexArrayFormat format, String positionVbo, String...enabledVertexAttribsIdentifiers) {
		
		super( shaderManager, format, enabledVertexAttribsIdentifiers );
		
		FloatBuffer verticesBuffer = null;
		IntBuffer indicesBuffer = null;
		
		try {
			
			verticesBuffer = MemoryUtil.memAllocFloat( 8 );
			indicesBuffer = MemoryUtil.memAllocInt( this.setIndicesCount( 6 ) );
			
			verticesBuffer.put( -1 ).put( -1 );
			verticesBuffer.put( -1 ).put( 1 );
			verticesBuffer.put( 1 ).put( 1 );
			verticesBuffer.put( 1 ).put( -1 );
			
			indicesBuffer.put( 0 ).put( 1 ).put( 3 );
			indicesBuffer.put( 1 ).put( 2 ).put( 3 );
			
			verticesBuffer.flip();
			indicesBuffer.flip();
			
			this.bindVao();
			this.uploadVboData( positionVbo, verticesBuffer, BufferUsage.STATIC_DRAW );
			this.uploadIboData( indicesBuffer, BufferUsage.STATIC_DRAW );
			
		} finally {
			
			if ( verticesBuffer != null ) MemoryUtil.memFree( verticesBuffer );
			if ( indicesBuffer != null ) MemoryUtil.memFree( indicesBuffer );
			
		}
		
	}

}
