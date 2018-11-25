package msengine.client.controller;

import msengine.common.controller.Controller;
import msengine.common.entity.EntityLiving;

public class PlayerController extends Controller<EntityLiving> {
	
	private final Window window;
	
	public PlayerController(EntityLiving entity) {
		
		super(entity);
		
		this.window = Window.getInstance();
		
	}
	
	@Override
	public void update() {
		
		World world = this.entity.getWorld();
		
		if ( this.window.isKeyPressed( this.game.optionMoveLeft.getKeyCode() ) ) {
			this.entity.walk( EntityDirection.LEFT );
		} else if ( this.window.isKeyPressed( this.game.optionMoveRight.getKeyCode() ) ) {
			this.entity.walk( EntityDirection.RIGHT );
		} else {
			this.entity.stopWalk();
		}
		
		if ( this.window.isKeyPressed( this.game.optionCrouch.getKeyCode() ) ) {
			// TODO Player crouch
		} else if ( this.window.isKeyPressed( this.game.optionJump.getKeyCode() ) ) {
			this.entity.jump();
		}
		
		// world.spawnParticles( SmokeParticle.class, this.entity.getPosX(), this.entity.getPosY() + 1.8f, 1, 1f, 1f );
		
	}

}
