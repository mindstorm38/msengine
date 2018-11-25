package msengine.common.controller;

import msengine.common.entity.Entity;

/**
 * 
 * @author Mindstorm38
 *
 * @param <T> Entity type controlled
 */
public abstract class Controller<T extends Entity> {
	
	protected final Algo game;
	
	protected final T entity;
	
	public Controller(T entity) {
		
		this.game = Algo.getInstance();
		
		this.entity = entity;
		
	}
	
	public abstract void update();
	
	public Entity getEntity() { return this.entity; }
	
}
