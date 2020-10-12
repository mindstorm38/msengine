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
	 * Start a {@link FrameRegulated} object.
	 * @param framed Regulated object.
	 * @param tps Numbers of ticks per second.
	 * @param fps Numbers of frames per second.
	 */
	static void regulateFrames(FrameRegulated framed, int tps, int fps) {
	
		int tpsInterval = (int) (1 / (float) tps * 1000);
		int fpsInterval = (int) (1 / (float) fps * 1000);
		
		long now, lastTime;
		long delta;
		int accumulator = 0;
		
		boolean running = true;
		
		lastTime = System.currentTimeMillis();
		
		do {
			
			if (framed.shouldStop()) {
				running = false;
			}
			
			now = System.currentTimeMillis();
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
