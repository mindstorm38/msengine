package io.msengine.client.graphics.texture;

import io.msengine.client.graphics.texture.base.TextureSetup;
import io.msengine.client.graphics.texture.metadata.AnimationInfo;
import io.msengine.client.graphics.texture.metadata.PredefinedMap;
import io.msengine.client.graphics.util.ImageUtils;
import io.msengine.common.asset.Asset;
import io.msengine.common.asset.metadata.MetadataParseException;
import org.lwjgl.system.MemoryUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;

public class MapTexture2D extends DynTexture2D {
	
	private static final Logger LOGGER = Logger.getLogger("mse.maptexture");
	
	private final Map<String, Tile> tiles = new HashMap<>();
	private final List<RunningAnimation> animations = new ArrayList<>();
	
	public MapTexture2D(TextureSetup setup) {
		super(setup);
	}
	
	/*public MapTexture2D(TextureSetup setup, Collection<Asset> assets, Function<String, String> tileNameExtractor) throws IOException {
		super(setup.withUnbind(false));
		this.buildMapAndUpload(assets, tileNameExtractor);
		setup.unbind(this);
	}
	
	public MapTexture2D(TextureSetup setup, Collection<Asset> assets) throws IOException {
		this(setup, assets, MapTexture2D::extractFileName);
	}*/
	
	public MapTexture2D() {
		this(SETUP_NEAREST);
	}
	
	/*public MapTexture2D(Collection<Asset> assets, Function<String, String> tileNameExtractor) throws IOException {
		this(SETUP_NEAREST, assets, tileNameExtractor);
	}
	
	public MapTexture2D(Collection<Asset> assets) throws IOException {
		this(SETUP_NEAREST, assets);
	}*/
	
	public Map<String, Tile> getTiles() {
		return this.tiles;
	}
	
	public Tile getTile(String name) {
		return this.tiles.get(name);
	}
	
	public Tile newTile(String name, float x, float y, float w, float h, String alias) {
		Tile tile = this.new Tile(x, y, w, h);
		this.tiles.put(name, tile);
		if (alias != null) {
			this.tiles.put(alias, tile);
		}
		return tile;
	}
	
	public Tile newTile(String name, float x, float y, float w, float h) {
		return this.newTile(name, x, y, w, h, null);
	}
	
	public void clearTiles() {
		this.tiles.clear();
		this.animations.forEach(RunningAnimation::close);
		this.animations.clear();
	}
	
	/**
	 * <p>Build this map buffer (of underlying {@link DynTexture2D}) from a group of assets
	 * by mapping them in a grid. This also support animation metadata.</p>
	 * <p>Assets that define a {@link AnimationInfo#META_SECTION}, an animation will be added, and
	 * can be updated with {@link #tick()}.</p>
	 * <p><b>This method do not upload the result.</b></p>
	 * @param assets The collection of assets to aggregate.
	 * @param tileNameExtractor An optional name extract used to name resulting {@link Tile}s objects.
	 * @throws IOException If the required texture size is too large for the maximum OpenGL texture size.
	 * @see #buildMapFromAssets(Collection)
	 * @see #getTile(String)
	 */
	public void buildMapFromAssets(Collection<Asset> assets, Function<String, String> tileNameExtractor) throws IOException {
		
		this.clearTiles();
		
		Map<String, TempImage> images = new HashMap<>();
		int[] maxTileSize = {0, 0};
		
		for (Asset asset : assets) {
			
			AnimationInfo animation;
			
			try {
				animation = asset.getMetadataSection(AnimationInfo.META_SECTION);
			} catch (MetadataParseException e) {
				LOGGER.log(Level.WARNING, "Failed to parse metadata section for MapTexture init, ignoring asset '" + asset + "' metadata.", e);
				animation = null;
			}
			
			String name;
			
			if (tileNameExtractor == null) {
				name = asset.getPath();
			} else {
				name = tileNameExtractor.apply(asset.getPath());
				if (name == null) {
					LOGGER.log(Level.WARNING, "The tile name extractor for path '" + asset.getPath() + "' returned null.");
					continue;
				}
			}
			
			AnimationInfo anim = animation;
			
			try {
				
				ImageUtils.loadImageFromStream(asset.openStreamExcept(), 4096, false, (buf, width, height) -> {
					
					if (anim != null) height /= anim.getFramesCount();
					if (width > maxTileSize[0]) maxTileSize[0] = width;
					if (height > maxTileSize[1]) maxTileSize[1] = height;
					
					images.put(name, new TempImage(buf, width, height, anim));
					
				});
				
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Failed to load image for MapTexture init, ignoring the asset '" + asset + "'.");
			}
			
		}
		
		if (maxTileSize[0] == 0) maxTileSize[0] = 1;
		if (maxTileSize[1] == 0) maxTileSize[1] = 1;
		
		maxTileSize[0] += 2;
		maxTileSize[1] += 2;
	
		int imageSize   = (int) Math.ceil(Math.sqrt(images.size()));
		int imageWidth  = imageSize * maxTileSize[0];
		int imageHeight = imageSize * maxTileSize[1];
		
		int maxTextureSize = getMaxTextureSize();
		
		if (imageWidth > maxTextureSize || imageHeight > maxTextureSize) {
			throw new IOException("Can't init the MapTexture since the texture is to large (max=" + maxTextureSize + ")");
		}
		
		this.allocImage(imageWidth, imageHeight);
		
		int column = 0;
		int row = 0;
		
		for (Map.Entry<String, TempImage> imageEntry : images.entrySet()) {
			
			TempImage image = imageEntry.getValue();
			ByteBuffer data = image.data;
			
			int x = column * maxTileSize[0] + 1;
			int y = row * maxTileSize[1] + 1;
			int width = image.width;
			int height = image.height;
			
			this.drawImage(x, y, width, height, data);
			this.drawImage(x - 1, y, 0, 0, 1, height, width, data); // Left
			this.drawImage(x, y + height, 0, height - 1, width, 1, width, data); // Bottom
			this.drawImage(x + width, y, width - 1, 0, 1, height, width, data); // Right
			this.drawImage(x, y - 1, 0, 0, width, 1, width, data); // Top
			
			this.newTile(
					imageEntry.getKey(),
					(float) x / imageWidth,
					(float) y / imageHeight,
					(float) width / imageWidth,
					(float) height / imageHeight
			);
			
			if (image.animation != null) {
				this.animations.add(new RunningAnimation(data, x, y, width, height, image.animation));
			} else {
				ImageUtils.freeImage(data);
			}
			
			if (++column >= imageSize) {
				column = 0;
				row++;
			}
			
		}
		
	}
	
	/**
	 * Same as {@link #buildMapFromAssets(Collection, Function)}, but with the <code>tileNameExtractor</code>
	 * argument set to a reference to method {@link #extractFileName(String)}.
	 * @param assets The collection of assets to aggregate.
	 * @throws IOException If the required texture size is too large for the maximum OpenGL texture size.
	 * @see #buildMapFromAssets(Collection, Function)
	 */
	public void buildMapFromAssets(Collection<Asset> assets) throws IOException {
		this.buildMapFromAssets(assets, MapTexture2D::extractFileName);
	}
	
	/**
	 * <p>Build this map buffer (of underlying {@link DynTexture2D}) from a <i>"predefined map"</i> asset.</p>
	 * <p><b>This method do not upload the result.</b></p>
	 * @param predefinedMapAsset The predefined map asset, to be recognized as this it must have a metadata
	 *                           section {@link PredefinedMap#META_SECTION} describing the tile size.
	 * @throws IllegalArgumentException If the asset do not define the metadata section.
	 * @throws IOException If the image can't be read from the asset.
	 */
	public void buildMapFromPredefined(Asset predefinedMapAsset) throws IOException, IllegalArgumentException {
		
		PredefinedMap map;
		
		try {
			map = predefinedMapAsset.getMetadataSection(PredefinedMap.META_SECTION);
			if (map == null) {
				throw new IllegalArgumentException("Invalid asset, no predefined map section ('map').");
			}
		} catch (MetadataParseException e) {
			throw new IllegalArgumentException("Invalid asset, failed to parse predefined map section ('map').", e);
		}
		
		this.allocFromAsset(predefinedMapAsset);
		
		int widthTilesCount = this.getWidth() / map.getTileWidth();
		int heightTilesCount = this.getHeight() / map.getTileHeight();
		float tileWidth = 1 / (float) widthTilesCount;
		float tileHeight = 1 / (float) heightTilesCount;
		System.out.println("tilesCount=" + widthTilesCount + "/" + heightTilesCount);
		for (int x = 0; x < widthTilesCount; ++x) {
			for (int y = 0; y < heightTilesCount; ++y) {
				String name = x + "/" + y;
				this.newTile(
						name,
						x * tileWidth,
						y * tileHeight,
						tileWidth,
						tileHeight,
						map.getAlias(name)
				);
			}
		}
		
	}
	
	/**
	 * Update all animation in this map, the texture is
	 * bound on the current texture unit.
	 */
	public void tick() {
		this.bind();
		this.animations.forEach(RunningAnimation::tick);
		unbind();
	}
	
	@Override
	public void close() {
		super.close();
		this.tiles.clear();
		this.animations.forEach(RunningAnimation::close);
		this.animations.clear();
	}
	
	public static String extractFileName(String name) {
		int slashIdx = name.lastIndexOf('/');
		int pointIdx = name.lastIndexOf('.');
		if (slashIdx == -1 && pointIdx == -1) {
			return name;
		} else if (pointIdx == -1) {
			return name.substring(slashIdx + 1);
		} else {
			return name.substring(slashIdx + 1, pointIdx);
		}
	}
	
	public class Tile {
		
		private final float x, y, width, height;
		
		private Tile(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public MapTexture2D getMap() {
			return MapTexture2D.this;
		}
		
		public float getX() {
			return this.x;
		}
		
		public float getY() {
			return this.y;
		}
		
		public float getWidth() {
			return this.width;
		}
		
		public float getHeight() {
			return this.height;
		}
		
		@Override
		public String toString() {
			return "MapTexture2D.Tile<pos=" + this.x + "/" + this.y + ", size=" + this.width + "/" + this.height + ">";
		}
		
	}
	
	private static class RunningAnimation implements Closeable {
		
		private ByteBuffer data;
		private final int x, y, width, height;
		private final int framesCount, tickSpeed;
		private int tickCounter, frameCounter;
		
		private RunningAnimation(ByteBuffer data, int x, int y, int width, int height, AnimationInfo animation) {
			this.data = data;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.framesCount = animation.getFramesCount();
			this.tickSpeed = animation.getTickSpeed();
		}
		
		private void tick() {
			if ((++this.tickCounter) >= this.tickSpeed) {
				this.frameCounter = (++this.frameCounter) % this.framesCount;
				this.upload();
				this.tickCounter = 0;
			}
		}
		
		private void upload() {
			if (this.data != null) {
				int frameSize = this.width * this.height;
				this.data.position(this.frameCounter * frameSize);
				this.data.limit(this.data.limit() + frameSize);
				glTexSubImage2D(GL_TEXTURE_2D, 0, this.x, this.y, this.width, this.height, GL_RGBA, GL_UNSIGNED_BYTE, this.data);
			}
		}
		
		@Override
		public void close() {
			if (this.data != null) {
				ImageUtils.freeImage(this.data);
				this.data = null;
			}
		}
		
	}
	
	private static class TempImage {
		private final ByteBuffer data;
		private final int width, height;
		private final AnimationInfo animation;
		public TempImage(ByteBuffer data, int width, int height, AnimationInfo animation) {
			this.data = data;
			this.width = width;
			this.height = height;
			this.animation = animation;
		}
	}

}
