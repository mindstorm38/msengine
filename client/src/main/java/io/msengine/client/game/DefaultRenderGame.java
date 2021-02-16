package io.msengine.client.game;

@Deprecated
public abstract class DefaultRenderGame<SELF extends DefaultRenderGame<SELF>> extends RenderGame<SELF, RenderGameOptions> {

	protected DefaultRenderGame(RenderGameOptions options) {
		super(options);
	}

}
