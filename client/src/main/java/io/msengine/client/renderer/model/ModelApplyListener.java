package io.msengine.client.renderer.model;

import org.joml.Matrix4f;

/**
 * 
 * Used by {@link ModelHandler} for {@link ModelHandler#apply()}
 * 
 * @author Mindstorm
 * 
 */
public interface ModelApplyListener {
	
	/**
	 * @param model Current {@link ModelHandler} matrix
	 */
	void modelApply(Matrix4f model);
	
}
