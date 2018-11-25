package io.msengine.common.game;

import io.sutil.CommonUtils;
import io.sutil.ThreadUtils;

public abstract class ServerGame<E extends ServerGameOptions> extends BaseGame<E> {

	public static final float DEFAULT_TPS = 20f;

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
	
	@Override
	protected void init() {
		
	}

	@Override
	protected void setuploop() {
		
		
		
	}
	
	@Override
	protected void loop() {
		
		this.now = CommonUtils.getTime();
		
		this.tick();
		
		while ( CommonUtils.getTime() - this.now < this.tpsInterval )
			ThreadUtils.safesleep( 1L );
		
	}

	@Override
	protected void stop() {
		
	}
	
	protected abstract void tick();

}
