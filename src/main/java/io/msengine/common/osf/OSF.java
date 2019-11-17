package io.msengine.common.osf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import io.msengine.common.osf.serializer.OSFDeserializationContext;
import io.msengine.common.osf.serializer.OSFSerializationContext;
import io.msengine.common.osf.serializer.OSFTypeAdapter;
import io.sutil.StreamUtils;
import io.sutil.StringUtils;

/**
 * 
 * Object Serialization Format (OSF)
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class OSF implements OSFSerializationContext, OSFDeserializationContext {
	
	// Constants \\
	
	public static final byte CURRENT_VERSION = 0x01;
	
	// Class \\
	
	private final Map<Class<?>, OSFTypeAdapter<?>> typeAdapters;
	
	public OSF() {
		
		this.typeAdapters = new HashMap<>();
		
	}
	
	public <T> OSF registerTypeAdapter(OSFTypeAdapter<T> serializer) {
		Class<T> clazz = serializer.initOSF( this );
		if ( clazz == null ) return this;
		this.typeAdapters.put( clazz, serializer );
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> OSFTypeAdapter<T> getTypeAdapter(Class<T> clazz) {
		return (OSFTypeAdapter<T>) this.typeAdapters.get( clazz );
	}
	
	private void throwTypeAdapterNotFound(Class<?> clazz) {
		throw new IllegalStateException( "TypeAdapter not found for " + clazz.getName() );
	}
	
	@Override
	public <T> OSFNode serialize(T obj, Class<T> clazz) {
		
		OSFTypeAdapter<T> adapter = this.getTypeAdapter( clazz );
		
		if ( adapter == null ) this.throwTypeAdapterNotFound( clazz );
		
		return adapter.serialize( obj, this );
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> OSFNode serialize(T obj) {
		return this.serialize( obj, (Class<T>) obj.getClass() );
	}
	
	@Override
	public <T> T deserialize(OSFNode node, Class<T> clazz) {
		
		OSFTypeAdapter<T> adapter = this.getTypeAdapter( clazz );
		
		if ( adapter == null ) this.throwTypeAdapterNotFound( clazz );
		
		return adapter.deserialize( node, this );
		
	}
	
	/**
	 * Save an object using specific type adapter class to an output stream
	 * @param stream The output stream
	 * @param obj The object to serialize
	 * @throws IOException See {@link #save(OutputStream, OSFNode)}
	 */
	public void save(OutputStream stream, Object obj) throws IOException {
		this.save( stream, this.serialize( obj ) );
	}
	
	/**
	 * Save an object using its default type adapter to an output stream
	 * @param stream The output stream
	 * @param obj The object to serialize
	 * @param clazz The type adapter class
	 * @throws IOException See {@link #save(OutputStream, OSFNode)}
	 */
	public <T> void save(OutputStream stream, T obj, Class<T> clazz) throws IOException {
		this.save( stream, this.serialize( obj, clazz ) );
	}
	
	/**
	 * Save a node to an output stream
	 * @param stream The output stream
	 * @param node The node to save
	 * @throws IOException Thrown by streams methods
	 */
	public void save(OutputStream stream, OSFNode node) throws IOException {

		stream.write( CURRENT_VERSION );
		GZIPOutputStream zipOut = new GZIPOutputStream( stream );
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		this.saveNode( out, node);
		zipOut.write( out.toByteArray() );
		zipOut.close();
		
	}
	
	/**
	 * Internal method to save a node to a output
	 * @param out The data output
	 * @param node The node to save
	 */
	private void saveNode(ByteArrayDataOutput out, OSFNode node) {
		
		if ( node.isObject() ) {
			
			OSFObject obj = node.getAsObject();
			
			out.writeByte( 0x00 );
			out.writeInt( obj.size() );
			
			for ( Entry<String, OSFNode> childEntry : obj.getChildren().entrySet() ) {

				String identifier = childEntry.getKey();
				
				byte[] identifierBytes = identifier.getBytes( StringUtils.CHARSET_UTF_8 );
				out.writeInt( identifierBytes.length );
				out.write( identifierBytes );
				
				this.saveNode( out, childEntry.getValue() );
				
			}
			
		} else if ( node.isArray() ) {
			
			OSFArray arr = node.getAsArray();
			
			if ( arr.isBooleanArray() ) {
				
				out.writeByte( 0x12 );
				out.writeInt( arr.size() );
				for ( OSFNode child : arr.getChildren() ) out.writeBoolean( child.getAsBoolean().getBoolean() );
				
			} else if ( arr.isByteArray() ) {
				
				out.writeByte( 0x13 );
				out.writeInt( arr.size() );
				for ( OSFNode child : arr.getChildren() ) out.writeByte( child.getAsNumber().getAsByte() );
				
			} else if ( arr.isShortArray() ) {
				
				out.writeByte( 0x14 );
				out.writeInt( arr.size() );
				for ( OSFNode child : arr.getChildren() ) out.writeShort( child.getAsNumber().getAsShort() );
				
			} else if ( arr.isIntegerArray() ) {
				
				out.writeByte( 0x15 );
				out.writeInt( arr.size() );
				for ( OSFNode child : arr.getChildren() ) out.writeInt( child.getAsNumber().getAsInteger() );
				
			} else if ( arr.isLongArray() ) {
				
				out.writeByte( 0x16 );
				out.writeInt( arr.size() );
				for ( OSFNode child : arr.getChildren() ) out.writeLong( child.getAsNumber().getAsLong() );
				
			} else if ( arr.isFloatArray() ) {
				
				out.writeByte( 0x17 );
				out.writeInt( arr.size() );
				for ( OSFNode child : arr.getChildren() ) out.writeFloat( child.getAsNumber().getAsFloat() );
				
			} else if ( arr.isDoubleArray() ) {
				
				out.writeByte( 0x18 );
				out.writeInt( arr.size() );
				for ( OSFNode child : arr.getChildren() ) out.writeDouble( child.getAsNumber().getAsDouble() );
				
			} else {
				
				out.writeByte( 0x01 );
				out.writeInt( arr.size() );
				for ( OSFNode child : arr.getChildren() ) this.saveNode( out, child );
				
			}
			
		} else if ( node.isBoolean() ) {
			
			out.writeByte( 0x02 );
			out.writeByte( node.getAsBoolean().getBoolean() ? 1 : 0 );
			
		} else if ( node.isNumber()) {
			
			OSFNumber number = node.getAsNumber();
			
			if ( number.isByte() ) {
				
				out.writeByte( 0x03 );
				out.writeByte( number.getAsByte() );
				
			} else if ( number.isShort() ) {
				
				out.writeByte( 0x04 );
				out.writeShort( number.getAsShort() );
				
			} else if ( number.isInteger() ) {
				
				out.writeByte( 0x05 );
				out.writeInt( number.getAsInteger() );
				
			} else if ( number.isLong() ) {
				
				out.writeByte( 0x06 );
				out.writeLong( number.getAsLong() );
				
			} else if ( number.isFloat() ) {
				
				out.writeByte( 0x07 );
				out.writeFloat( number.getAsFloat() );
				
			} else if ( number.isDouble() ) {
				
				out.writeByte( 0x08 );
				out.writeDouble( number.getAsDouble() );
				
			}
			
		} else if ( node.isText() ) {
			
			String text = node.getAsText().getText();
			
			out.writeByte( 0x09 );
			byte[] textBytes = text.getBytes( StringUtils.CHARSET_UTF_8 );
			out.writeInt( textBytes.length );
			out.write( textBytes );
			
		}
		
	}
	
	/**
	 * Read a serialized object from an input stream
	 * @param stream The input stream
	 * @param clazz The type adapter class
	 * @return The deserialized object
	 * @throws IOException Thrown by streams methods
	 */
	public <T> T read(InputStream stream, Class<T> clazz) throws IOException {
		
		OSFNode node = this.read( stream );
		if ( node == null ) return null;
		
		return this.deserialize( node, clazz );
		
	}
	
	/**
	 * Read a node from an input stream
	 * @param stream The input stream
	 * @return The node read from this input stream
	 * @throws IOException Thrown by streams methods
	 */
	public OSFNode read(InputStream stream) throws IOException {

		byte version = (byte) stream.read();
		
		GZIPInputStream zipIn = new GZIPInputStream( stream );
		ByteArrayDataInput in = ByteStreams.newDataInput( StreamUtils.getStreamByteArray( zipIn ) );
		zipIn.close();
		
		return this.readNode( in, version );
		
	}
	
	private OSFNode readNode(ByteArrayDataInput in, byte version) {
		
		if ( version == 0x01 ) {
			
			byte nodeIndex = in.readByte();
			
			if ( nodeIndex == 0x00 ) {
				
				OSFObject osf = new OSFObject();
				
				int size = in.readInt();
				
				for ( int i = 0; i < size; i++ ) {
					
					int identifierLength = in.readInt();
					byte[] identifierBytes = new byte[ identifierLength ];
					in.readFully( identifierBytes );
					String identifier = new String( identifierBytes, StringUtils.CHARSET_UTF_8 );
					
					OSFNode child = this.readNode( in, version );
					
					if ( child != null ) {
						
						osf.set( identifier, child );
						
					}
					
				}
				
				return osf;
				
			} else if ( nodeIndex == 0x01 ) {
				
				OSFArray osf = new OSFArray();
				
				int size = in.readInt();
				
				for ( int i = 0; i < size; i++ ) {
					
					OSFNode child = this.readNode( in, version );
					
					if ( child != null ) {
						
						osf.add( child );
						
					}
					
				}
				
				return osf;
				
			} else if ( nodeIndex == 0x12 ) {
				
				OSFArray osf = new OSFArray();
				int size = in.readInt();
				for ( int i = 0; i < size; i++ ) osf.add( new OSFBoolean( in.readBoolean() ) );
				return osf;
				
			} else if ( nodeIndex == 0x13 ) {
				
				OSFArray osf = new OSFArray();
				int size = in.readInt();
				for ( int i = 0; i < size; i++ ) osf.add( new OSFNumber( in.readByte() ) );
				return osf;
				
			} else if ( nodeIndex == 0x14 ) {
				
				OSFArray osf = new OSFArray();
				int size = in.readInt();
				for ( int i = 0; i < size; i++ ) osf.add( new OSFNumber( in.readShort() ) );
				return osf;
				
			} else if ( nodeIndex == 0x15 ) {
				
				OSFArray osf = new OSFArray();
				int size = in.readInt();
				for ( int i = 0; i < size; i++ ) osf.add( new OSFNumber( in.readInt() ) );
				return osf;
				
			} else if ( nodeIndex == 0x16 ) {
				
				OSFArray osf = new OSFArray();
				int size = in.readInt();
				for ( int i = 0; i < size; i++ ) osf.add( new OSFNumber( in.readLong() ) );
				return osf;
				
			} else if ( nodeIndex == 0x17 ) {
				
				OSFArray osf = new OSFArray();
				int size = in.readInt();
				for ( int i = 0; i < size; i++ ) osf.add( new OSFNumber( in.readFloat() ) );
				return osf;
				
			} else if ( nodeIndex == 0x18 ) {
				
				OSFArray osf = new OSFArray();
				int size = in.readInt();
				for ( int i = 0; i < size; i++ ) osf.add( new OSFNumber( in.readDouble() ) );
				return osf;
				
			} else if ( nodeIndex == 0x02 ) {
				return new OSFBoolean( in.readByte() == 1 ? true : false );
			} else if ( nodeIndex == 0x03 ) {
				return new OSFNumber( in.readByte() );
			} else if ( nodeIndex == 0x04 ) {
				return new OSFNumber( in.readShort() );
			} else if ( nodeIndex == 0x05 ) {
				return new OSFNumber( in.readInt() );
			} else if ( nodeIndex == 0x06 ) {
				return new OSFNumber( in.readLong() );
			} else if ( nodeIndex == 0x07 ) {
				return new OSFNumber( in.readFloat() );
			} else if ( nodeIndex == 0x08 ) {
				return new OSFNumber( in.readDouble() );
			} else if ( nodeIndex == 0x09 ) {
				
				int textLength = in.readInt();
				byte[] textBytes = new byte[ textLength ];
				in.readFully( textBytes );
				String text = new String( textBytes, StringUtils.CHARSET_UTF_8 );
				
				return new OSFText( text );
				
			}
			
		}
		
		return null;
		
	}

}
