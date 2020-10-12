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
	 * Start a {@link TickRegulated} object.
	 * @param ticked Regulated object.
	 * @param tps Numbers of ticks per second.
	 */
	static void regulateTick(TickRegulated ticked, int tps) {
		
		int tpsInterval = (int) (1 / (float) tps * 1000);
		
		long now;
		boolean running = true;
		
		do {
			
			now = System.currentTimeMillis();
			
			if (ticked.shouldStop()) {
				running = false;
			}
			
			ticked.tick();
			
			sleepAccurate(now, tpsInterval);
			
		} while (running);
		
	}
	
	/**
	 * Accurate sleeping, used in {@link TickRegulated#regulateTick(TickRegulated, int)} and
	 * @param start Start time for sleeping.
	 * @param time Time to sleep.
	 */
	static void sleepAccurate(long start, int time) {
		while (System.currentTimeMillis() - start < time) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
