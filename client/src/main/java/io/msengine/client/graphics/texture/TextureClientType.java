package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.util.DataType;

public class TextureClientType {

	public static final TextureClientType BYTE = fromDataType(DataType.BYTE);
	public static final TextureClientType UBYTE = fromDataType(DataType.UBYTE);
	public static final TextureClientType SHORT = fromDataType(DataType.SHORT);
	public static final TextureClientType USHORT= fromDataType(DataType.USHORT);
	public static final TextureClientType INT = fromDataType(DataType.INT);
	public static final TextureClientType UINT= fromDataType(DataType.UINT);
	public static final TextureClientType FLOAT = fromDataType(DataType.FLOAT);
	public static final TextureClientType HFLOAT = fromDataType(DataType.HFLOAT);

	// Class //

	public final int value;

	public TextureClientType(int value) {
		this.value = value;
	}

	private static TextureClientType fromDataType(DataType dataType) {
		return new TextureClientType(dataType.value);
	}

}
