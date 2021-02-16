package io.msengine.common.util;

/**
 * Static helpers class for faster maths and more graphics-related functions.
 */
public final class MathHelper {
	
	private MathHelper() { }
	
	public static final double PI_TWICE = Math.PI * 2.0;
	public static final double PI_HALF = Math.PI / 2.0;
	
	public static int floorFloatInt(float value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}
	
	public static int floorDoubleInt(double value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}
	
	public static long floorDoubleLong(double value) {
		long i = (long) value;
		return value < i ? i - 1L : i;
	}
	
	public static int ceilingFloatInt(float value) {
        int i = (int) value;
        return value > i ? i + 1 : i;
    }
	
    public static int ceilingDoubleInt(double value) {
        int i = (int) value;
        return value > i ? i + 1 : i;
    }
	
    public static double distanceInt(int x1, int y1, int x2, int y2) {
    	int d1 = (x1 - x2);
    	int d2 = (y1 - y2);
    	return Math.sqrt((d1 * d1) + (d2 * d2));
    }
	
    public static double distanceFloat(float x1, float y1, float x2, float y2) {
    	float d1 = (x1 - x2);
    	float d2 = (y1 - y2);
    	return Math.sqrt((d1 * d1) + (d2 * d2));
    }
	
    public static float map(float value, float inInf, float inSup, float outInf, float outSup, boolean clamp) {
    	float ret = (value - inInf) / (inSup - inInf) * (outSup - outInf) + outInf;
    	if (!clamp) return ret;
    	if (outInf < outSup) {
    		return clamp(value, outInf, outSup);
    	} else {
    		return clamp(value, outSup, outInf);
    	}
    }
    
    public static float map(float value, float inInf, float inSup, float outInf, float outSup) {
    	return map(value, inInf, inSup, outInf, outSup, false);
    }
    
    public static float clamp(float value, float min, float max) {
		return (value < min) ? min : (value > max) ? max : value;
    }
    
    public static float lerp(float from, float to, float alpha) {
		return (to == from) ? to : ((to - from) * alpha) + from;
	}
	
	public static double lerp(double from, double to, float alpha) {
		return (to == from) ? to : (( to - from) * alpha) + from;
	}
	
}