package msengine.common.entity;

public enum EntityDirection {
	
	LEFT ( (byte) -1 ),
	RIGHT ( (byte) 1 );
	
	public final byte scale;
	
	private EntityDirection(byte scale) {
		this.scale = scale;
	}
	
	public EntityDirection oposite() {
		return oposite( this );
	}
	
	public static EntityDirection oposite(EntityDirection direction) {
		return direction == LEFT ? RIGHT : LEFT;
	}
	
	public static EntityDirection fromScale(int scale) {
		return scale > 0 ? RIGHT : LEFT;
	}
	
}
