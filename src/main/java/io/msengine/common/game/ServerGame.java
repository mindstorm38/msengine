package io.msengine.common.game;

import io.msengine.common.util.GameTypeRequired;
import io.sutil.CommonUtils;
import io.sutil.ThreadUtils;

public abstract class ServerGame<E extends ServerGameOptions> extends BaseGame<E> {

	// Constants \\
	
	public static final float DEFAULT_TPS = 20f;

	// Static \\
	
	public static ServerGame<?> getCurrentServer() {
		BaseGame<?> s = getCurrent();
		if ( !( s instanceof ServerGame ) ) throw new GameTypeRequired( ServerGame.class );
		return (ServerGame<?>) s;
	}
	
	// Class \\
	
	protected double now;
	protected float tpsInterval;
	
	public ServerGame(E options) {
		
		super( options );
		
		this.setTPS( DEFAULT_TPS );
		
	}
	
	public double now() {
		return this.now;
	}
	
	public void setTPS(float tps) {
		this.tpsInterval = 1f / ( tps == 0f ? DEFAULT_TPS : tps );
	}
	
	public float getTPSInterval() {
		return this.tpsInterval;
	}
	
	@Override
	protected void init() {
		
		super.init();
		
	}

	@Override
	protected void setuploop() {
		
		super.setuploop();
		
	}
	
	@Override
	protected void loop() {
		
		this.now = CommonUtils.getTime();
		
		this.profiler.startSection("update");
		
		this.update();
		
		this.profiler.endStartSection("sync");
		
		while ( CommonUtils.getTime() - this.now < this.tpsInterval )
			ThreadUtils.safesleep( 1L );
		
		this.profiler.endSection();
		
	}

	@Override
	protected void stop() {
		
		super.stop();
		
	}
	
	protected abstract void update();

	/**
	 * Update a {@link GameTimed} implementation.
	 * @param timed The implementation
	 */
	public void updateTimed(GameTimed timed) {
		timed.setTime( this.now );
	}
	
}
