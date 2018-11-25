package msengine.common.particle;

import java.util.List;
import java.util.Random;

import io.sutil.registry.NamespaceRegistry;
import msengine.common.util.AxisAlignedBB;

public abstract class Particle {
	
	// Registry \\
	
	public static final NamespaceRegistry<Class<? extends Particle>> REGISTRY = new NamespaceRegistry<>();
	
	static {
		
		
		
	}
	
	// Class \\
	
	protected final World world;
	protected final Random rand;
	
	protected String stateIdentifier;
	
	protected float posX;
	protected float posY;
	
	protected float lastPosX;
	protected float lastPosY;
	
	protected float velX;
	protected float velY;
	
	protected boolean dead;
	protected long lifetime;
	
	protected AxisAlignedBB boundingBox;
	
	public Particle(World world) {
		
		this.world = world;
		this.rand = world.getRandom();
		
		this.stateIdentifier = null;
		
		this.dead = false;
		this.lifetime = 0L;
		
		this.boundingBox = new AxisAlignedBB( -0.01f, -0.01f, 0.01f, 0.01f );
		
	}
	
	public void initPosition(float x, float y) {
		
		this.boundingBox = this.boundingBox.offset( x - this.posX, y - this.posY );
		
		this.lastPosX = this.posX;
		this.lastPosY = this.posY;
		
		this.resetPositionToBoundingBox();
		
	}
	
	public World getWorld() { return this.world; }
	
	public float getPosX() { return this.posX; }
	public float getPosY() { return this.posY; }
	
	public float getLastPosX() { return this.lastPosX; }
	public float getLastPosY() { return this.lastPosY; }
	
	public float getVelX() { return this.velX; }
	public float getVelY() { return this.velY; }
	
	public boolean isDead() { return this.dead; }
	public void kill() { this.dead = true; }
	
	/**
	 * @return Particle lifetime
	 */
	public long getLifetime() { return this.lifetime; }
	
	/**
	 * Update particle velocity
	 * @return Particle state identifier used, or null if don't render the particle
	 */
	public abstract String updateState();
	
	/**
	 * Update the particle entity each tick
	 */
	public void update() {
		
		this.lastPosX = this.posX;
		this.lastPosY = this.posY;
		
		this.stateIdentifier = this.updateState();
		
		this.move( this.velX, this.velY );
		
		this.lifetime++;
		
	}
	
	public void move(float x, float y) {
		
		List<AxisAlignedBB> bbs = this.world.getTilesBoundingBoxesList( this.boundingBox.addCoord( x, y ) );
		
		if ( y != 0 ) {
			
			for ( AxisAlignedBB bb : bbs ) {
				y = bb.calculateYOffset( this.boundingBox, y );
			}
			
			this.boundingBox = this.boundingBox.offset( 0, y );
			
		}
		
		if ( x != 0 ) {
			
			for ( AxisAlignedBB bb : bbs ) {
				x = bb.calculateXOffset( this.boundingBox, x );
			}
			
			if ( x != 0 ) {
				this.boundingBox = this.boundingBox.offset( x, 0 );
			}
			
		}
		
		if ( x == 0 ) this.velX = 0;
		if ( x == 0 ) this.velY = 0;
		
		this.resetPositionToBoundingBox();
		
	}
	
	public void resetPositionToBoundingBox() {
		
		this.posX = ( this.boundingBox.minX + this.boundingBox.maxX ) / 2f;
		this.posY = ( this.boundingBox.minY + this.boundingBox.maxY ) / 2f;
		
	}
	
	/**
	 * @return Current particle state
	 */
	public String getStateIdentifier() {
		return this.stateIdentifier;
	}
	
	/**
	 * @return Is this particle renderable
	 */
	public boolean renderable() {
		return this.stateIdentifier != null;
	}
	
}
