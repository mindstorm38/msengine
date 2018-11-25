package msengine.common.entity;

import net.algo.osf.OSFObject;
import net.algo.world.World;

public abstract class EntityLiving extends EntityMotion {
	
	// Class \\
	
	protected float health;
	
	protected float fallHeight;
	
	protected float walkSpeed;
	protected int limbAnimFrame;
	
	protected EntityDirection direction;
	
	public EntityLiving(World world) {
		
		super(world);
		
		this.health = 1f;
		
		this.fallHeight = 0f;
		
		this.walkSpeed = 0f;
		this.limbAnimFrame = 0;
		
		this.direction = EntityDirection.RIGHT;
		
	}
	
	@Override
	public OSFObject save() {
		OSFObject osf = super.save();
		osf.setFloat( "health", this.health );
		osf.setByte( "direction", this.direction.scale );
		return osf;
	}
	
	@Override
	public void load(OSFObject serialized) {
		
		super.load(serialized);
		
		this.health = serialized.getFloat( "health", this.health );
		this.direction = EntityDirection.fromScale( serialized.getByte( "direction", this.direction.scale ) );
		
	}
	
	@Override
	public void update() {
		
		boolean lastOnGround = this.onGround;
		float lastVelY = this.velY;
		
		this.velX = this.direction.scale * this.walkSpeed;
		
		if ( this.walkSpeed > 0 ) {
			
			this.limbAnimFrame++;
			if ( this.limbAnimFrame > 8 ) this.limbAnimFrame = 0;
			
			// System.out.println( this.limbAnimFrame );
			
		} else if ( this.limbAnimFrame != 0 ) this.limbAnimFrame = 0;
		
		super.update();
		
		if ( this.onGround && !lastOnGround ) {
			
			float fallDamage = this.getFallDamage( this.fallHeight );
			
			this.fallHeight = 0f;
			
			if ( fallDamage > 1f ) {
				
				this.health -= fallDamage;
				
			}
			
		} else if ( ( this.fallHeight > 0 || lastVelY >= 0 ) && this.velY < 0 ) {
			
			this.fallHeight += this.lastPosY - this.posY;
			
		}
		
		if ( this.health <= 0f ) {
			
			this.kill();
			
			return;
			
		}
		
	}
	
	// - Health
	public float getHealth() { return this.health; }
	public void setHealth(float health) { this.health = health; }
	
	public void damage(float amount) {
		
		this.health -= amount;
		
	}
	
	// - Jump
	public boolean canJump() {
		return this.onGround;
	}
	
	public float getJumpPower() {
		return 0.7f;
	}
	
	public void jump(float power) {
		
		if ( this.canJump() ) {
			
			this.velY += power;
			
		}
		
	}
	
	public void jump() {
		this.jump( this.getJumpPower() );
	}
	
	// - Walk
	public float getWalkSpeed() {
		return 0.1f;
	}
	
	public void walk(EntityDirection direction) {
		this.direction = direction;
		this.walk();
	}
	
	public void walk(float speed) {
		this.walkSpeed = speed;
	}
	
	public void walk() {
		this.walk( this.getWalkSpeed() );
	}
	
	public void stopWalk() {
		this.walk( 0f );
	}
	
	// - Fall
	public float getFallSensibility() {
		return 2.7f;
	}
	
	public float getFallDamage(float fallHeight) {
		float damage = fallHeight * this.getFallSensibility() * this.world.getGravityFactor();
		return damage * damage;
	}
	
	public float getMinFallForDamage() {
		return 1f / ( this.getFallSensibility() * this.world.getGravityFactor() );
	}
	
	public float getFallHeight() { return this.fallHeight; }
	
	public int getLimbAnimFrame() { return this.limbAnimFrame; }
	
	public EntityDirection getDirection() { return this.direction; }
	public void setDirection(EntityDirection direction) { this.direction = direction; } 
	
}
