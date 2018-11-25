package msengine.common.entity;

import java.util.List;

import net.algo.osf.OSFObject;
import net.algo.util.math.AxisAlignedBB;
import net.algo.world.World;

public abstract class EntityMotion extends Entity {

	protected float lastPosX;
	protected float lastPosY;
	
	protected float velX;
	protected float velY;
	
	protected boolean onGround;
	
	protected boolean noClip;
	
	public EntityMotion(World world) {
		
		super(world);
		
		this.lastPosX = 0f;
		this.lastPosY = 0f;
		
		this.velX = 0f;
		this.velY = 0f;
		
		this.onGround = false;
		
		this.noClip = false;
		
	}
	
	@Override
	public OSFObject save() {
		OSFObject osf = super.save();
		osf.setFloat( "vel_x", this.velX );
		osf.setFloat( "vel_y", this.velY );
		osf.setBoolean( "no_clip", this.noClip );
		return osf;
	}
	
	@Override
	public void load(OSFObject serialized) {
		
		super.load(serialized);
		
		this.velX = serialized.getFloat( "vel_x", this.velX );
		this.velY = serialized.getFloat( "vel_y", this.velY );
		this.noClip = serialized.getBoolean( "no_clip", this.noClip );
		
	}
	
	@Override
	public void update() {
		
		super.update();
		
		this.lastPosX = this.posX;
		this.lastPosY = this.posY;
		
		this.velY -= this.world.getGravityFactor();
		
		this.move( this.velX, this.velY );
		
	}
	
	public boolean isNoClip() { return this.noClip; }
	public void setNoClip(boolean noClip) { this.noClip = noClip; }
	
	@Override
	public void setPosition(float x, float y) {
		
		super.setPosition(x, y);
		
		this.lastPosX = this.posX;
		this.lastPosY = this.posY;
		
	}
	
	/**
	 * Trying to move the entity towards this position
	 * @param x X offset position
	 * @param y Y offset position
	 */
	public void move(float x, float y) {
		
		if ( this.noClip ) {
			
			this.setBoundingBox( this.getBoundingBox().offset( x, y ) );
			this.resetPositionToBoundingBox();
			
		} else {
			
			List<AxisAlignedBB> bbs = this.world.getTilesBoundingBoxesList( this.getBoundingBox().addCoord( x, y ) );
			
			if ( y != 0 ) {
				
				boolean down = y < 0;
				
				for ( AxisAlignedBB bb : bbs ) {
					y = bb.calculateYOffset( this.getBoundingBox(), y );
				}
				
				this.setBoundingBox( this.getBoundingBox().offset( 0, y ) );
				
				this.onGround = ( down && y == 0 );
				
			}
			
			if ( x != 0 ) {
				
				for ( AxisAlignedBB bb : bbs ) {
					x = bb.calculateXOffset( this.getBoundingBox(), x );
				}
				
				if ( x != 0 ) {
					this.setBoundingBox( this.getBoundingBox().offset( x, 0 ) );
				}
				
			}
			
			if ( x == 0 ) this.velX = 0;
			if ( y == 0 ) this.velY = 0;
			
			this.resetPositionToBoundingBox();
			
		}
		
	}
	
	public float getLastPosX() { return this.lastPosX; }
	public float getLastPosY() { return this.lastPosY; }
	
	public float getVelX() { return this.velX; }
	public void setVelX(float velX) { this.velX = velX; }
	
	public float getVelY() { return this.velY; }
	public void setVelY(float velY) { this.velY = velY; }
	
	public boolean isOnGround() { return this.onGround; }
	
}
