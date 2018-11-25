package msengine.common.entity;

import java.util.Random;

import io.sutil.registry.NamespaceRegistry;
import msengine.common.osf.OSFObject;
import msengine.common.util.AxisAlignedBB;

public abstract class Entity {
	
	// Registry \\
	
	public static final NamespaceRegistry<Class<? extends Entity>> REGISTRY = new NamespaceRegistry<>();
	
	static {
		
		
		
	}
	
	// Class \\
	
	private final String identifier;
	
	protected final World world;
	protected final Random rand;
	
	protected float posX;
	protected float posY;
	
	public int zIndex;
	
	protected AxisAlignedBB boundingBox;
	
	protected boolean dead;
	protected long lifetime;
	
	protected Controller<?> controller;
	
	public Entity(World world) {
		
		this.identifier = REGISTRY.getKey( this.getClass() );
		if ( this.identifier == null ) throw new IllegalStateException( "First register this entity class '" + this.getClass().getName() + "' to Entity.REGISTRY" );
		
		this.world = world;
		this.rand = world.getRandom();
		
		this.posX = 0f;
		this.posY = 0f;
		
		this.resetZIndex();
		
		this.boundingBox = new AxisAlignedBB();
		
		this.dead = false;
		this.lifetime = 0L;
		
		this.setupDefaultController();
		
	}
	
	/**
	 * Serializing entity for saving
	 * @return Serialized entity as {@link OSFObject}
	 */
	public OSFObject save() {
		OSFObject osf = new OSFObject();
		osf.setFloat( "x", this.posX  );
		osf.setFloat( "y", this.posY );
		osf.setInt( "z_index", this.zIndex );
		return osf;
	}
	
	/**
	 * Load serialized entity {@link OSFObject} (used by {@link WorldManager#loadWorld(WorldMetadata)}
	 * @param serialized Serialized {@link OSFObject} of the entity
	 */
	public void load(OSFObject serialized) {
		
		this.setPosition( serialized.getFloatRequired("x"), serialized.getFloatRequired("y") );
		
		this.zIndex = serialized.getInt( "z_index", this.zIndex );
		
	}
	
	public final String getIdentifier() { return this.identifier; }
	
	/**
	 * Function executed each tick to update current entity
	 */
	public void update() {
		
		this.lifetime++;
		
		this.updateController();
		
	}
	
	public AxisAlignedBB getBoundingBox() { return this.boundingBox; }
	public void setBoundingBox(AxisAlignedBB bb) { this.boundingBox = bb; }
	
	public void resetPositionToBoundingBox() {
		
		AxisAlignedBB bb = this.getBoundingBox();
		
		this.posX = ( bb.minX + bb.maxX ) / 2f;
		this.posY = bb.minY;
		
	}
	
	public World getWorld() { return this.world; }
	
	public float getPosX() { return this.posX; }
	public float getPosY() { return this.posY; }
	
	public boolean isDead() { return this.dead; }
	public void kill() { this.dead = true; }
	
	public long getLifetime() { return this.lifetime; }
	
	protected void setupDefaultController() {
		this.controller = null;
	}
	
	private void updateController() {
		if ( this.controller != null ) this.controller.update();
	}
	
	public void setController(Controller<?> controller) {
		this.controller = controller;
	}
	
	public void setPosition(float x, float y) {
		
		this.setBoundingBox( this.getBoundingBox().offset( x - this.posX, y - this.posY ) );
		this.resetPositionToBoundingBox();
		
	}
	
	public void setPositionX(float x) {
		this.setPosition( x, this.posY );
	}
	
	public void setPositionY(float y) {
		this.setPosition( this.posX, y );
	}
	
	/**
	 * Reset to base z-index for this entity
	 */
	public void resetZIndex() {
		this.zIndex = 0;
	}
	
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}
	
}
