package io.msengine.client.graphics.font.glyph;

public class Glyph {

	private final int codePoint;
	private final float tx0, ty0, tx1, ty1;
	private final float px0, py0, px1, py1;
	private final float advance;
	
	public Glyph(int codePoint, float tx0, float ty0, float tx1, float ty1, float px0, float py0, float px1, float py1, float advance) {
		
		this.codePoint = codePoint;
		
		this.tx0 = tx0;
		this.ty0 = ty0;
		this.tx1 = tx1;
		this.ty1 = ty1;
		
		this.px0 = px0;
		this.py0 = py0;
		this.px1 = px1;
		this.py1 = py1;
		
		this.advance = advance;
		
	}
	
	public int getCodePoint() {
		return this.codePoint;
	}
	
	public float getAdvance() {
		return this.advance;
	}
	
	public float getKernAdvance(int nextCodePoint) {
		return this.advance;
	}
	
	public void forEachCorner(CornerConsumer consumer) {
		consumer.accept(px0, py0, tx0, ty0);
		consumer.accept(px0, py1, tx0, ty1);
		consumer.accept(px1, py1, tx1, ty1);
		consumer.accept(px1, py0, tx1, ty0);
	}
	
	@FunctionalInterface
	public interface CornerConsumer {
		void accept(float x, float y, float tx, float ty);
	}
	
}
