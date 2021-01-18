package io.msengine.common.game;

@Deprecated
public abstract class DefaultServerGame<SELF extends DefaultServerGame<SELF>> extends ServerGame<SELF, ServerGameOptions> {

	public DefaultServerGame(ServerGameOptions options) {
		super( options );
	}

}
