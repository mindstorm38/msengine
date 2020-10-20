package io.msengine.client.graphics.texture.asset;

import io.msengine.client.graphics.texture.Texture;

public abstract class AssetTexture extends Texture {
	
	@Override
	protected int getTarget() {
		return 0;
	}
	
}
