package io.msengine.common.logic;

@SuppressWarnings("unused")
public interface FrameRegulated extends TickRegulated {
	
	/**
	 * Called each `1/tps` seconds.
	 * @param alpha Interpolation value used when `tps > fps`.
	 */
	void render(float alpha);
	
	/**
	 * Return the sync value.
	 * @return True to sync in case of rendering not synchronized vertically.
	 */
	boolean mustSync();
	
	/**
	 * Start this {@link FrameRegulated} object.
	 * @param tps Number of ticks per second.
	 * @param fps Number of frames per second.
	 */
	default void regulateFrames(int tps, int fps) {
		regulateFrames(this, tps, fps);
	}
	
	/**
	 * Start a {@link FrameRegulated} object.
	 * @param framed Regulated object.
	 * @param tps Number of ticks per second.
	 * @param fps Number of frames per second.
	 */
	static void regulateFrames(FrameRegulated framed, int tps, int fps) {
		
		if (tps < 1) {
			throw new IllegalArgumentException("Illegal TPS: " + tps + " < 1");
		} else if (fps < 1) {
			throw new IllegalArgumentException("Illegal FPS: " + fps + " < 1");
		}
		
		long tpsInterval = (long) (10e9 / (double) tps);
		long fpsInterval = (long) (10e9 / (double) fps);
		
		long now, lastTime;
		long delta;
		long accumulator = 0;
		
		boolean running = true;
		
		lastTime = System.nanoTime();
		
		do {
			
			if (framed.shouldStop()) {
				running = false;
			}
			
			now = System.nanoTime();
			delta = now - lastTime;
			accumulator += delta;
			
			while (accumulator >= tpsInterval) {
				framed.tick();
				accumulator -= tpsInterval;
			}
			
			framed.render((float) accumulator / tpsInterval);
			
			if (framed.mustSync()) {
				TickRegulated.sleepAccurate(now, fpsInterval);
			}
			
			lastTime = now;
			
		} while (running);
		
	}
	
}
