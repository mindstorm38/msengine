package io.msengine.client.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

@Deprecated
public class Utils {
	
	public static ByteBuffer getImageBuffer(BufferedImage image, int x, int y, int width, int height) {
		
		if ( image == null ) throw new NullPointerException( "Image must not be null" );
		
		// Checking validity of x, y, width or height arguments
		if ( x < 0 || y < 0 || ( x + width ) > image.getWidth() || ( y + height ) > image.getHeight() ) throw new IndexOutOfBoundsException("x, y, width or height argument is invalid");
		
		// Retrieving pixels of image
		int[] pixels = new int[ width * height ];
		image.getRGB( x, y, width, height, pixels, 0, width );
		
		ByteBuffer buffer = BufferUtils.createByteBuffer( width * height * 4 );
		
		for ( int iy = 0; iy < height; iy++ ) {
			for ( int ix = 0; ix < width; ix++ ) {
				int pixel = pixels[ iy * width + ix ];
				buffer.put( (byte) ( ( pixel >> 16 ) & 0xFF) );
				buffer.put( (byte) ( ( pixel >> 8 ) & 0xFF) );
				buffer.put( (byte) ( pixel & 0xFF ) );
				buffer.put( (byte) ( ( pixel >> 24 ) & 0xFF) );
			}
		}
		
		buffer.flip();
		
		return buffer;
		
	}
	
	public static ByteBuffer getImageBuffer(BufferedImage image) {
		return getImageBuffer( image, 0, 0, image.getWidth(), image.getHeight() );
	}
	
	public static BufferedImage getBufferImage(ByteBuffer buffer, int width, int height) {
		
		BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		
		int[] pixels = new int[ width * height ];
		
		int bufidx;
		for ( int i = 0; i < pixels.length; i++ ) {
			
			bufidx = i * 4;
			
			pixels[ i ] =
					( buffer.get( bufidx + 0 ) & 0xFF ) << 16	|
					( buffer.get( bufidx + 1 ) & 0xFF ) << 8 |
					( buffer.get( bufidx + 2 ) & 0xFF ) << 0 |
					( buffer.get( bufidx + 3 ) & 0xFF ) << 24;
			
		}
		
		image.setRGB( 0, 0, width, height, pixels, 0, width );
		
		AffineTransform at = AffineTransform.getScaleInstance( 1, -1 );
		at.translate( 0, -height );
		
		AffineTransformOp op = new AffineTransformOp( at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
		
		return op.filter( image, null );
		
	}
	
	public static void incrementBufferPosition(Buffer buf, int count) {
		buf.position( buf.position() + count );
	}
	
}

