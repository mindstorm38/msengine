package io.msengine.client.renderer.texture;

import io.msengine.client.renderer.texture.metadata.AnimationMetadataSection;

import java.nio.ByteBuffer;

public class TextureMapAnimation {
	
	private final TextureMap map;
	private final ByteBuffer[] framesBuffers;
	private final int imgX, imgY, imgWidth, imgHeight;
	private final int framesCount;
	private final int tickSpeed;
	
	private int tickCounter;
	private int frameCounter;
	
	TextureMapAnimation(TextureMap map, int imgX, int imgY, int imgWidth, int imgHeight, AnimationMetadataSection animation) {
		
		this.map = map;
		this.framesBuffers = new ByteBuffer[animation.getFramesCount()];
		this.imgX = imgX;
		this.imgY = imgY;
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
		this.framesCount = animation.getFramesCount();
		this.tickSpeed = animation.getTickSpeed();
		
		this.tickCounter = 0;
		this.frameCounter = 0;
		
	}
	
	public void tick() {
		
		if ((++this.tickCounter) == this.tickSpeed) {
			
			this.frameCounter = (++this.frameCounter) % this.framesCount;
			this.uploadFrame();
			
		}
		
	}
	
	private void uploadFrame() {
		this.map.getTextureObject().upload(this.framesBuffers[this.frameCounter], this.imgX, this.imgY, this.imgWidth, this.imgHeight);
	}
	
	ByteBuffer[] getFramesBuffers() {
		return this.framesBuffers;
	}
	
	public int getImgX() {
		return imgX;
	}
	
	public int getImgY() {
		return imgY;
	}
	
	public int getImgWidth() {
		return imgWidth;
	}
	
	public int getImgHeight() {
		return imgHeight;
	}
	
	public int getFramesCount() {
		return this.framesCount;
	}
	
	public int getTickSpeed() {
		return this.tickSpeed;
	}
	
}
