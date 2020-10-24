package io.msengine.client.graphics.texture.base;

public interface SamplerBase {
	
	void setMinFilter(SamplerParamFilter minFilter);
	void setMagFilter(SamplerParamFilter magFilter);
	void setWidthWrap(SamplerParamWrap widthWrap);
	void setHeightWrap(SamplerParamWrap heightWrap);
	void setDepthWrap(SamplerParamWrap depthWrap);
	void setMaxAnisotropy(float maxAnisotropy);
	
}
