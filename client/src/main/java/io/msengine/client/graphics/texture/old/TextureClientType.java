package io.msengine.client.graphics.texture.old;

import io.msengine.client.graphics.util.DataType;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public class TextureClientType {

	public static final TextureClientType BYTE = forDataType(DataType.BYTE);
	public static final TextureClientType UBYTE = forDataType(DataType.UBYTE);
	public static final TextureClientType SHORT = forDataType(DataType.SHORT);
	public static final TextureClientType USHORT= forDataType(DataType.USHORT);
	public static final TextureClientType INT = forDataType(DataType.INT);
	public static final TextureClientType UINT= forDataType(DataType.UINT);
	public static final TextureClientType FLOAT = forDataType(DataType.FLOAT);
	public static final TextureClientType HFLOAT = forDataType(DataType.HFLOAT);
	
	public static final TextureClientType UBYTE_3_3_2           = forSpecial(GL_UNSIGNED_BYTE_3_3_2, GL_RGB);
	public static final TextureClientType UBYTE_3_3_2_REV       = forSpecialRev(GL_UNSIGNED_BYTE_2_3_3_REV, GL_RGB);
	public static final TextureClientType USHORT_5_6_5          = forSpecial(GL_UNSIGNED_SHORT_5_6_5, GL_RGB);
	public static final TextureClientType USHORT_5_6_5_REV      = forSpecialRev(GL_UNSIGNED_SHORT_5_6_5_REV, GL_RGB);
	public static final TextureClientType USHORT_4_4_4_4        = forSpecial(GL_UNSIGNED_SHORT_4_4_4_4, GL_RGBA);
	public static final TextureClientType USHORT_4_4_4_4_REV    = forSpecialRev(GL_UNSIGNED_SHORT_4_4_4_4_REV, GL_RGBA);
	public static final TextureClientType USHORT_5_5_5_1        = forSpecial(GL_UNSIGNED_SHORT_5_5_5_1, GL_RGBA);
	public static final TextureClientType USHORT_5_5_5_1_REV    = forSpecialRev(GL_UNSIGNED_SHORT_1_5_5_5_REV, GL_RGBA);
	public static final TextureClientType UINT_8_8_8_8          = forSpecial(GL_UNSIGNED_INT_8_8_8_8, GL_RGBA);
	public static final TextureClientType UINT_8_8_8_8_REV      = forSpecialRev(GL_UNSIGNED_INT_8_8_8_8_REV, GL_RGBA);
	public static final TextureClientType UINT_10_10_10_2       = forSpecial(GL_UNSIGNED_INT_10_10_10_2, GL_RGBA);
	public static final TextureClientType UINT_2_10_10_10       = forSpecialRev(GL_UNSIGNED_INT_2_10_10_10_REV, GL_RGBA);
	
	public static final TextureClientType UINT_24_8             = forSpecial(GL_UNSIGNED_INT_24_8, GL_DEPTH_STENCIL);
	public static final TextureClientType UINT_10F_11F_11F_REV  = forSpecialRevInternal(GL_UNSIGNED_INT_10F_11F_11F_REV, GL_RGB, GL_R11F_G11F_B10F);
	public static final TextureClientType UINT_5_9_9_9_REV      = forSpecialRevInternal(GL_UNSIGNED_INT_5_9_9_9_REV, GL_RGB, GL_RGB9_E5);
	
	// Class //

	public final int value;
	public final boolean reversed;
	public final int clientFormat;
	public final int internalFormat;

	public TextureClientType(int value, boolean reversed, int clientFormat, int internalFormat) {
		this.value = value;
		this.reversed = reversed;
		this.clientFormat = clientFormat;
		this.internalFormat = internalFormat;
	}

	public static TextureClientType forDataType(DataType dataType) {
		return new TextureClientType(dataType.value, false, 0, 0);
	}
	
	public static TextureClientType forSpecial(int value, int clientFormat) {
		return new TextureClientType(value, false, clientFormat, 0);
	}
	
	public static TextureClientType forSpecialRev(int value, int clientFormat) {
		return new TextureClientType(value, true, clientFormat, 0);
	}
	
	public static TextureClientType forSpecialRevInternal(int value, int clientFormat, int internalFormat) {
		return new TextureClientType(value, true, clientFormat, internalFormat);
	}
	
	public boolean isValidFor(int clientFormat, int internalFormat) {
		return (this.clientFormat == 0 || this.clientFormat == clientFormat) &&
				(this.internalFormat == 0 || this.internalFormat == internalFormat);
	}

}
