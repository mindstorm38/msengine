package io.msengine.common.resource;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.lwjgl.BufferUtils;

import io.msengine.common.resource.metadata.Metadata;
import io.sutil.ImageUtils;
import io.sutil.LazyLoadValue;
import io.sutil.StreamUtils;
import io.sutil.StringUtils;
import io.sutil.resource.Resource;

public class DetailledResource extends Resource {
	
	private final LazyLoadValue<InputStream> resourceMetaInputStream;
	private final Metadata metadata;
	
	DetailledResource(Resource resource) {
		
		super( resource.getAccessor(), resource.getPath() );
		
		this.resourceMetaInputStream = new LazyLoadValue<InputStream>() {
			
			public InputStream create() {
				return DetailledResource.this.accessor.resourceInputStream( ResourceManager.getResourceMetaPath( DetailledResource.this.path ) );
			}
			
		};
		
		this.metadata = new Metadata( this );
		
	}
	
	/**
	 * Read {@link ByteBuffer} from this resource stream<br>
	 * This function close the stream at the end of reading
	 * @return Java NIO {@link ByteBuffer} allocated with {@link BufferUtils#createByteBuffer(int)} or null if error with {@link StreamUtils#getStreamByteArraySafe(InputStream)}
	 * @see StreamUtils#getStreamByteArraySafe(InputStream)
	 * @see BufferUtils#createByteBuffer(int)
	 */
	public ByteBuffer getByteBuffer() {
		
		byte[] bytes = StreamUtils.getStreamByteArraySafe( this.inputStream.get() );
		
		if ( bytes == null ) return null;
		
		ByteBuffer buffer = BufferUtils.createByteBuffer( bytes.length );
		buffer.position( 0 );
		buffer.put( bytes );
		return buffer;
		
	}
	
	/**
	 * Get the {@link BufferedImage} from this resource stream<br>
	 * This function close the stream at the end of reading
	 * @return Image corresponding to this resource or null if unknown type
	 * @see ImageUtils#readBufferedImageSafe(InputStream)
	 */
	public BufferedImage getImage() {
		return ImageUtils.readBufferedImageSafe( this.inputStream.get() );
	}
	
	/**
	 * Read string from this resource stream<br>
	 * This function close the stream at the end of reading
	 * @param charset Read charset
	 * @return String represented by this stream using specified charset
	 */
	public String getText(Charset charset) {
		
		byte[] bytes = StreamUtils.getStreamByteArraySafe( this.inputStream.get() );
		if ( bytes == null ) return null;
		return new String( bytes, charset );
		
	}
	
	/**
	 * UTF-8 version of {@link #getText(Charset)}
	 * @return String represented by this stream using UTF-8 charset
	 * @see #getText(Charset)
	 */
	public String getText() {
		return this.getText( StringUtils.CHARSET_UTF_8 );
	}
	
	public boolean hasMetadata() {
		return this.metadata.valid();
	}
	
	public Metadata getMetadata() {
		return this.metadata;
	}
	
	public InputStream getMetadataInputStream() {
		return this.resourceMetaInputStream.get();
	}
	
	@Override
	public void close() throws Exception {
		
		super.close();
		
		if ( this.resourceMetaInputStream.loaded() )
			StreamUtils.safeclose( this.resourceMetaInputStream.get() );
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.close();
	}

}
