package msengine.common.entity;

import net.algo.osf.OSFObject;
import net.algo.world.World;

public abstract class EntityTile extends Entity {
	
	protected float rotation;
	
	public EntityTile(World world) {
		
		super(world);
		
		this.rotation = 0f;
		
	}
	
	@Override
	public OSFObject save() {
		OSFObject osf = super.save();
		osf.setFloat( "rotation", this.rotation );
		return osf;
	}
	
	@Override
	public void load(OSFObject serialized) {
		
		super.load(serialized);
		
		this.rotation = serialized.getFloat( "rotation", this.rotation );
		
	}
	
	public float getRotation() {
		return this.rotation;
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
}
