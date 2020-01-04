package io.msengine.common.game;

public abstract class DefaultServerGame<SELF extends DefaultServerGame<SELF>> extends ServerGame<SELF, ServerGameOptions> {

	public DefaultServerGame(ServerGameOptions options) {
		super( options );
	}

}
