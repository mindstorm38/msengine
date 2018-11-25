package msengine.client.util;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

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
	
}
