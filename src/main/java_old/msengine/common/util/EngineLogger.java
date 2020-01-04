package msengine.common.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.sutil.LoggerUtils;

public class EngineLogger {
	
	public static final Logger LOGGER;
	
	static {
		
		LOGGER = Logger.getLogger("MSE");
		LOGGER.setUseParentHandlers( true );
		LOGGER.setParent( LoggerUtils.DEFAULT_PARENT_LOGGER );
		LOGGER.setLevel( Level.ALL );
		LoggerUtils.setParentLogger( LOGGER );
		
	}
	
}
