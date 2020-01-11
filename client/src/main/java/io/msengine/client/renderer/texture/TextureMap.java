package io.msengine.client.renderer.texture;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.imageio.ImageIO;

import io.msengine.client.renderer.util.RenderConstantFields;
import io.msengine.client.renderer.texture.metadata.AnimationMetadataSection;
import io.msengine.client.util.Utils;
import io.msengine.common.game.BaseGame;
import io.msengine.common.resource.DetailledResource;
import io.msengine.common.resource.ResourceManager;
import io.msengine.common.resource.metadata.MetadataSerializer;
import io.sutil.FileUtils;
import io.sutil.StreamUtils;

import static io.msengine.common.util.GameLogger.LOGGER;

public class TextureMap extends TextureMapBase<TextureMapTile> {
	
	// Constants \\
	
	public static final Function<String, String> PNG_FILTER = path ->
			path.endsWith(".png") ? path.substring( 0, path.length() - 4 ) : null;
	
	// Static \\
	
	public static boolean debugAtlases = false;
	
	public static void setDebugAtlases(boolean debug) {
		debugAtlases = debug;
	}
	
	static {
		
		if (!MetadataSerializer.getInstance().hasMetadataSectionType(AnimationMetadataSection.class))
			MetadataSerializer.getInstance().registerMetadataSectionType(new AnimationMetadataSection.Serializer(), AnimationMetadataSection.class);
		
	}
	
	// Class \\
	
	private final Function<String, String> filter;
	private BufferedImage atlasCached;
	
	private final HashSet<TextureMapAnimation> animated;
	
	public TextureMap(String path, Function<String, String> filter) {
		
		super( path );
		
		this.filter = filter;
		this.atlasCached = null;
		
		this.animated = new HashSet<>();
		
	}
	
	@Override
	public void loadTexture(ResourceManager resourceManager) throws IOException {
		
		this.delete();
		
		this.atlasTiles.clear();
		this.animated.clear();
		
		// Retrieving all texture images
		List<DetailledResource> resources = resourceManager.listDetailledResources(this.path);
		
		if (resources == null) {
			
			LOGGER.warning("Can't get resources for texture map '" + this.path + "'");
			return;
			
		}
		
		Map<String, RawSprite> images = new HashMap<>();
		
		int maxTileWidth = 0;
		int maxTileHeight = 0;
		
		for ( DetailledResource res : resources ) {
			
			String path = res.getName();
			
			if (this.filter != null)
				if ((path = this.filter.apply(path)) == null)
					continue;
			
			BufferedImage bi = res.getImage();
			AnimationMetadataSection animation = res.hasMetadata() ? res.getMetadata().getMetadataSection("animation") : null;
			
			StreamUtils.safeclose(res);
			
			if (bi == null) {
				LOGGER.warning("Unable to load texture for a map at '" + path + "'");
			} else {
				
				int biWidth = bi.getWidth();
				int biHeight = animation == null ? bi.getHeight() : (bi.getHeight() / animation.getFramesCount());
				
				if (biWidth > maxTileWidth) maxTileWidth = biWidth;
				if (biHeight > maxTileHeight) maxTileHeight = biHeight;
				
				images.put(path, new RawSprite(bi, biWidth, biHeight, animation));
				
			}
			
		}
		
		if (maxTileWidth == 0) maxTileWidth = 1;
		if (maxTileHeight == 0) maxTileHeight = 1;
		
		// Missing texture
		BufferedImage missingTextureImgRaw = RenderConstantFields.getInstance().getMissingTexture().getImage();
		BufferedImage missingTextureImg = new BufferedImage(maxTileWidth, maxTileHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D missingTextureGraphics = missingTextureImg.createGraphics();
		missingTextureGraphics.drawImage(missingTextureImgRaw, 0, 0, maxTileWidth, maxTileHeight, null);
		missingTextureGraphics.dispose();
		images.put(null, new RawSprite(missingTextureImg));
		
		maxTileWidth += 2;
		maxTileHeight += 2;
		
		int imageSize = (int) Math.ceil(Math.sqrt(images.size()));
		int imageWidth = imageSize * maxTileWidth;
		int imageHeight = imageSize * maxTileHeight;
		
		// Drawing atlas
		int maxTextureSize = RenderConstantFields.getInstance().getMaxTextureSize();
		
		if (imageWidth > maxTextureSize || imageHeight > maxTextureSize) {
			throw new IOException("Wanted texture size is to large (" + maxTextureSize + ")");
		}
		
		BufferedImage atlas = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D atlasGraphics = atlas.createGraphics();
		
		int column = 0;
		int row = 0;
		
		for (Entry<String, RawSprite> imageEntry : images.entrySet()) {
			
			RawSprite sprite = imageEntry.getValue();
			BufferedImage bi = sprite.getImage();
			
			if (bi != null) {
				
				int x = column * maxTileWidth + 1;
				int y = row * maxTileHeight + 1;
				int width = sprite.getWidth();
				int height = sprite.getHeight();
				
				atlasGraphics.drawImage( bi, x, y, width, height, null ); // Center
				atlasGraphics.drawImage( bi, x - 1, y, x, y + height, 0, 0, 1, height, null ); // Left
				atlasGraphics.drawImage( bi, x, y + height, x + width, y + height + 1, 0, height - 1, width, height, null ); // Bottom
				atlasGraphics.drawImage( bi, x + width, y, x + width + 1, y + height, width - 1, 0, width, height, null ); // Right
				atlasGraphics.drawImage( bi, x, y - 1, x + width, y, 0, 0, width, 1, null ); // Top
				
				TextureMapTile tile = new TextureMapTile(
						this,
						(float) x / imageWidth,
						(float) y / imageHeight,
						(float) width / imageWidth,
						(float) height / imageHeight
				);
				
				if (sprite.isAnimated()) {
					
					AnimationMetadataSection animation = sprite.getAnimation();
					int framesCount = animation.getFramesCount();
					
					TextureMapAnimation mapAnimation = new TextureMapAnimation(this, x, y, width, height, animation);
					
					for (int f = 0; f < framesCount; ++f) {
						mapAnimation.getFramesBuffers()[f] = Utils.getImageBuffer(bi, 0, f * height, width, height);
					}
					
					this.animated.add(mapAnimation);
					
				}
				
				this.atlasTiles.put(imageEntry.getKey(), tile);
				
			}
			
			if ((++column) >= imageSize) {
				column = 0;
				row++;
			}
			
		}
		
		atlasGraphics.dispose();
		
		if (debugAtlases) {
			
			File debugFile = new File(BaseGame.getCurrent().getAppdata(), FileUtils.getFileName(this.path) + "_atlas.png");
			ImageIO.write(atlas, "png", debugFile);
			
		}
		
		this.texture = new TextureObject(atlas);
		this.atlasCached = atlas;
		
	}
	
	@Override
	public TextureMapTile getTile(String path) {
		return this.atlasTiles.containsKey( path ) ? this.atlasTiles.get( path ) : this.atlasTiles.get( null );
	}
	
	public void tick() {
		this.animated.forEach(TextureMapAnimation::tick);
	}
	
	private static class RawSprite {
		
		private final BufferedImage image;
		private final int width, height;
		private final AnimationMetadataSection animation;
		
		RawSprite(BufferedImage image, int width, int height, AnimationMetadataSection animation) {
			
			this.image = image;
			this.width = width;
			this.height = height;
			this.animation = animation;
			
		}
		
		RawSprite(BufferedImage image) {
			this(image, image.getWidth(), image.getHeight(), null);
		}
		
		BufferedImage getImage() {
			return this.image;
		}
		
		int getWidth() {
			return this.width;
		}
		
		int getHeight() {
			return this.height;
		}
		
		AnimationMetadataSection getAnimation() {
			return this.animation;
		}
		
		boolean isAnimated() {
			return this.animation != null;
		}
		
	}
	
}
