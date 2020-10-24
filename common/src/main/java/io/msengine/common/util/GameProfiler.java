package io.msengine.common.util;

import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.profiler.Profiler;

@Deprecated
public class GameProfiler extends Profiler {
	
	// Singleton \\
	
	private static GameProfiler INSTANCE = null;
	
	public static GameProfiler getInstance() {
		if ( INSTANCE == null ) new GameProfiler();
		return INSTANCE;
	}
	
	// Class \\
	
	private GameProfiler() {
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( GameProfiler.class );
		INSTANCE = this;
		
	}
	
}
