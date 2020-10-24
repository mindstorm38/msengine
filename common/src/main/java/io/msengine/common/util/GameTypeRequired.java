package io.msengine.common.util;

@Deprecated
public class GameTypeRequired extends RuntimeException {

	private static final long serialVersionUID = -4950800764406277602L;
	
	public GameTypeRequired(Class<?> requiredGameClass) {
		super( "This action requires a " + requiredGameClass.getSimpleName() + " game type" );
	}
	
}
