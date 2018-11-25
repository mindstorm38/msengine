package msengine.common.controller;

import java.util.Random;

import msengine.common.entity.EntityMotion;

public abstract class AIController<T extends EntityMotion> extends Controller<T> {

	protected final Random random;
	
	public AIController(T entity) {
		
		super(entity);
		
		this.random = new Random();
		
	}

}
