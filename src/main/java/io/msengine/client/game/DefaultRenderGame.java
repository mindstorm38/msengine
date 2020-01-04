package io.msengine.client.game;

public abstract class DefaultRenderGame<SELF extends DefaultRenderGame<SELF>> extends RenderGame<SELF, RenderGameOptions> {

	protected DefaultRenderGame(RenderGameOptions options) {
		super(options);
	}

}
