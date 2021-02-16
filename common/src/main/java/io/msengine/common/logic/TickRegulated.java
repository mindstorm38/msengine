package io.msengine.common.logic;

@SuppressWarnings("unused")
public interface TickRegulated {
	
	/**
	 * Called each `1/tps` seconds.
	 */
	void tick();
	
	/**
	 * Use this to stop the regulation.
	 * @return True to stop the ticking loop.
	 */
	boolean shouldStop();
	
	/**
	 * Start this {@link TickRegulated object}.
	 * @param tps Number of ticks per second.
	 */
	default void regulateTick(int tps) {
		regulateTick(this, tps);
	}
	
	/**
	 * Start a {@link TickRegulated} object.
	 * @param ticked Regulated object.
	 * @param tps Number of ticks per second.
	 */
	static void regulateTick(TickRegulated ticked, int tps) {
		
		if (tps < 1) {
			throw new IllegalArgumentException("Illegal TPS: " + tps + " < 1");
		}
		
		long tpsInterval = (long) (1e9 / (double) tps);
		
		long now;
		boolean running = true;
		
		do {
			
			now = System.nanoTime();
			
			if (ticked.shouldStop()) {
				running = false;
			}
			
			ticked.tick();
			
			sleepAccurate(now, tpsInterval);
			
		} while (running);
		
	}
	
	/**
	 * Accurate sleeping, used in {@link TickRegulated#regulateTick(TickRegulated, int)} and
	 * @param start Start time for sleeping (ns).
	 * @param time Time to sleep (ns).
	 */
	static void sleepAccurate(long start, long time) {
		while (System.nanoTime() - start < time) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
