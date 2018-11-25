package io.msengine.client.renderer.model;

import org.joml.Matrix4f;

/**
 * 
 * Used to replace deprecated {@link GL11#glPushMatrix()} and {@link GL11#glPopMatrix()}.
 * 
 * @author Mindstorm38
 * 
 */
public class ModelHandler {
	
	private final ModelApplyListener listener;
	private Matrix4f[] models = new Matrix4f[0];
	
	/**
	 * @param listener {@link ModelApplyListener} to use for {@link ModelHandler#modelApply()}
	 */
	public ModelHandler(ModelApplyListener listener) {
		
		this.listener = listener;
		
	}
	
	/**
	 * Push model matrix. (like {@link GL11#glPushMatrix()})
	 * This function add (if the array is not empty) on the top of the array a copy of the current last matrix.
	 * If array is empty, creating a new identity matrix.
	 * @return This {@link ModelHandler} instance.
	 */
	public ModelHandler push() {
		
		if ( this.models.length == 0 ) {
			
			this.models = new Matrix4f[] { new Matrix4f() };
			
		} else {
			
			Matrix4f[] newModels = new Matrix4f[ this.models.length + 1 ];
			System.arraycopy( this.models, 0, newModels, 0, this.models.length );
			newModels[ this.models.length ] = new Matrix4f( newModels[ this.models.length - 1 ] );
			this.models = newModels;
			
		}
		
		return this;
		
	}
	
	/**
	 * Translate current pushed matrix. Shortcut for {@link Matrix4f#translate(x, y, z)}
	 * @param x Translate X parameter
	 * @param y Translate Y parameter
	 * @param z Translate Z parameter
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#translate(float, float, float)
	 */
	public ModelHandler translate(float x, float y, float z) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].translate( x, y, z );
		return this;
		
	}
	
	/**
	 * 2D mode of {@link #translate(float, float, float)}
	 * @param x Translate X parameter
	 * @param y Translate Y parameter
	 * @return This {@link ModelHandler} instance.
	 */
	public ModelHandler translate(float x, float y) {
		return this.translate( x, y, 0 );
	}
	
	/**
	 * Translate current pushed matrix on x axis
	 * @param x Translate amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#translate(float, float, float)
	 */
	public ModelHandler translateX(float x) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].translate( x, 0, 0 );
		return this;
		
	}
	
	/**
	 * Translate current pushed matrix on y axis
	 * @param y Translate amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#translate(float, float, float)
	 */
	public ModelHandler translateY(float y) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].translate( 0, y, 0 );
		return this;
		
	}
	
	/**
	 * Translate current pushed matrix on z axis
	 * @param z Translate amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#translate(float, float, float)
	 */
	public ModelHandler translateZ(float z) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].translate( 0, 0, z );
		return this;
		
	}
	
	/**
	 * Scale current pushed matrix. Shortcut {@link Matrix4f#scale(x, y, z)}
	 * @param x Scale X parameter
	 * @param y Scale Y parameter
	 * @param z Scale Z parameter
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float, float, float)
	 */
	public ModelHandler scale(float x, float y, float z) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].scale( x, y, z );
		return this;
		
	}
	
	/**
	 * 2D mode of {@link #scale(float, float, float)}
	 * @param x Scale X parameter
	 * @param y Scale Y parameter
	 * @return This {@link ModelHandler} instance.
	 */
	public ModelHandler scale(float x, float y) {
		return this.scale( x, y, 0 );
	}
	
	/**
	 * Scale current pushed matrix on x axis
	 * @param x Scale amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float, float, float)
	 */
	public ModelHandler scaleX(float x) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].scale( x, 1, 1 );
		return this;
		
	}
	
	/**
	 * Scale current pushed matrix on y axis
	 * @param y Scale amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float, float, float)
	 */
	public ModelHandler scaleY(float y) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].scale( 1, y, 1 );
		return this;
		
	}
	
	/**
	 * Scale current pushed matrix on z axis
	 * @param z Scale amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float, float, float)
	 */
	public ModelHandler scaleZ(float z) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].scale( 1, 1, z );
		return this;
		
	}
	
	/**
	 * Scale current pushed matrix on all (x,y,z) axis
	 * @param xyz Scale amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float)
	 */
	public ModelHandler scale(float xyz) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].scale( xyz );
		return this;
		
	}
	
	/**
	 * Rotate current pushed matrix by angle in radian for all (x,y,z) selected axis
	 * @param ang Rotate angle (in radians)
	 * @param x X component axis
	 * @param y Y component axis
	 * @param z Z component axis
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#rotate(float, float, float, float)
	 */
	public ModelHandler rotate(float ang, float x, float y, float z) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].rotate( ang, x, y, z );
		return this;
		
	}
	
	/**
	 * Rotate current pushed matrix by angle in radian for x axis
	 * @param ang Rotate angle (in radians)
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#rotateX(float)
	 */
	public ModelHandler rotateX(float ang) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].rotateX( ang );
		return this;
		
	}
	
	/**
	 * Rotate current pushed matrix by angle in radian for y axis
	 * @param ang Rotate angle (in radians)
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#rotateY(float)
	 */
	public ModelHandler rotateY(float ang) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].rotateY( ang );
		return this;
		
	}
	
	/**
	 * Rotate current pushed matrix by angle in radian for z axis
	 * @param ang Rotate angle (in radians)
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#rotateZ(float)
	 */
	public ModelHandler rotateZ(float ang) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].rotateZ( ang );
		return this;
		
	}
	
	/**
	 * Rotate current pushed matrix around translated point (originX,originY,originZ) by angle in radian for all (x,y,z) selected axis
	 * @param originX Translated point X
	 * @param originY Translated point Y
	 * @param originZ Translated point Z
	 * @param ang Rotate angle (in radians)
	 * @param x X component axis
	 * @param y Y component axis
	 * @param z Z component axis
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#translate(float, float, float)
	 * @see Matrix4f#rotate(float, float, float, float)
	 */
	public ModelHandler rotateOrigin(float originX, float originY, float originZ, float ang, float x, float y, float z) {
		
		if ( this.models.length == 0 ) return this;
		this.models[ this.models.length - 1 ].translate( originX, originY, originZ ).rotate( ang, x, y, z ).translate( -originX, -originY, -originZ );
		return this;
		
	}
	
	/**
	 * Pop current model matrix. (like {@link GL11#glPopMatrix()})
	 * This function remove (only if the array as at least one matrix) the current matrix from the array. And (if one matrix remain) new using previous matrix.
	 * @return This {@link ModelHandler} instance.
	 */
	public ModelHandler pop() {
		
		if ( this.models.length == 0 ) return this;
		
		Matrix4f[] newModels = new Matrix4f[ this.models.length - 1 ];
		System.arraycopy( this.models, 0, newModels, 0, newModels.length );
		this.models = newModels;
		
		this.apply();
			
		return this;
		
	}
	
	/**
	 * @return Current model matrix, or null if no matrix as been initialized.
	 */
	public Matrix4f current() {
		if ( this.models.length == 0 ) return null;
		return this.models[ this.models.length - 1 ];
	}
	
	/**
	 * Applying current model matrix (do nothing if no matrix in the array) with {@link ModelApplyListener#modelApply(Matrix4f)}
	 * @return This {@link ModelHandler} instance.
	 */
	public ModelHandler apply() {
		
		if ( this.models.length == 0 ) this.listener.modelApply( null );
		else this.listener.modelApply( this.models[ this.models.length - 1 ] );
		return this;
		
	}
	
	/**
	 * Clearing this {@link ModelHandler} matrix array.
	 */
	public void reset() {
		this.models = new Matrix4f[0];
	}
	
}
