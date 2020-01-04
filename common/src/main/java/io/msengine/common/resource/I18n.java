package io.msengine.common.resource;

import io.msengine.common.util.GameNotCreatedException;
import io.sutil.SingletonAlreadyInstantiatedException;
import io.sutil.lang.LanguageManager;

public class I18n extends LanguageManager {
	
	// Singleton \\
	
	private static I18n INSTANCE = null;
	
	public static I18n getInstance() {
		if ( INSTANCE == null ) throw new GameNotCreatedException( I18n.class );
		return INSTANCE;
	}
	
	public static String get(String key, Object...params) {
		return INSTANCE.getEntry( key, params );
	}
	
	public static String get(String key) {
		return INSTANCE.getEntry( key );
	}
	
	// Class \\
	
	public I18n(String baseLangsFolderPath) {
		
		super( ResourceManager.getInstance(), baseLangsFolderPath );
		
		if ( INSTANCE != null ) throw new SingletonAlreadyInstantiatedException( I18n.class );
		INSTANCE = this;
		
	}
	
}
