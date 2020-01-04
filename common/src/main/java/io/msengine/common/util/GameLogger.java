package io.msengine.common.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.sutil.LoggerUtils;
import io.sutil.SingletonAlreadyInstantiatedException;

public class GameLogger {
	
	public static Logger LOGGER = null;
	
	public static Logger create(String name) {
		
		if ( LOGGER != null )
			throw new SingletonAlreadyInstantiatedException( GameLogger.class );
		
		LOGGER = Logger.getLogger( name );
		LOGGER.setUseParentHandlers( true );
		LOGGER.setParent( LoggerUtils.DEFAULT_PARENT_LOGGER );
		LOGGER.setLevel( Level.ALL );
		LoggerUtils.setParentLogger( LOGGER );
		
		return LOGGER;
		
	}
	
}
