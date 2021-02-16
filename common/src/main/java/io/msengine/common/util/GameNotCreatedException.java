package io.msengine.common.util;

@Deprecated
public class GameNotCreatedException extends RuntimeException {

	private static final long serialVersionUID = -3933714369521600755L;

	public GameNotCreatedException(Class<?> clazz) {
		super( "Can't get " + clazz.getSimpleName() + ", create game first." );
	}
	
}
