package io.msengine.client.renderer.model;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

/**
 * 
 * <p>Used to replace deprecated {@link GL11#glPushMatrix()} and {@link GL11#glPopMatrix()}
 * and the old render pipeline. You can now use this to update shader's model matrices.<br>
 * <b>Not Thread-Safe</b></p>
 *
 * <p><b>This class might be moved in {@link io.msengine.client.graphics} in the future.</b></p>
 * 
 * @author Théo Rozier (Mindstorm38)
 *
 */
public class ModelHandler {
	
	private static final int INITIAL_MODELS_CAPACITY = 1;
	
	private final ModelApplyListener listener;
	private final Matrix4f matrix;
	
	private float[] models;
	private int step = 0;
	
	/**
	 * @param listener {@link ModelApplyListener} to use for {@link ModelHandler#apply()}.
	 * @param initialModelsCapacity The initial number of models to allocate in internal models array,
	 *                              can be used to increase default capacity of 1 model and make faster
	 *                              first uses.
	 */
	public ModelHandler(ModelApplyListener listener, int initialModelsCapacity) {
		
		if (initialModelsCapacity < 0)
			throw new IllegalArgumentException("Invalid initial models capacity, can't be negative.");
		
		this.listener = listener;
		this.matrix = new Matrix4f();
		this.models = new float[initialModelsCapacity << 4];
		
	}
	
	/**
	 * Calling {@link ModelHandler#ModelHandler(ModelApplyListener, int)} with initial capacity of 1.
	 * @param listener {@link ModelApplyListener} to use for {@link ModelHandler#apply()}.
	 * @see #ModelHandler(ModelApplyListener, int)
	 */
	public ModelHandler(ModelApplyListener listener) {
		this(listener, INITIAL_MODELS_CAPACITY);
	}
	
	/**
	 * Push model matrix. (like {@link GL11#glPushMatrix()})
	 * This function add (if the array is not empty) on the top of the array a copy of the current last matrix.
	 * If array is empty, creating a new identity matrix.
	 * @return This {@link ModelHandler} instance.
	 */
	public ModelHandler push() {
		
		if (this.step == 0) {
			
			this.matrix.identity();
			this.step = 1;
			
		} else {
			
			int neededSize = this.step << 4;
			
			if (this.models.length < neededSize) {
				
				float[] nmodels = new float[neededSize];
				System.arraycopy(this.models, 0, nmodels, 0, this.models.length);
				this.models = nmodels;
				
			}
			
			this.matrix.get(this.models, neededSize - 16);
			++this.step;
			
		}
		
		/*
		if ( this.modelsMatrices.length == 0 ) {
			
			this.modelsMatrices = new Matrix4f[] { new Matrix4f() };
			
		} else {
			
			Matrix4f[] newModels = new Matrix4f[ this.modelsMatrices.length + 1 ];
			System.arraycopy( this.modelsMatrices, 0, newModels, 0, this.modelsMatrices.length );
			newModels[ this.modelsMatrices.length ] = new Matrix4f( newModels[ this.modelsMatrices.length - 1 ] );
			this.modelsMatrices = newModels;
			
		}
		*/
		
		return this;
		
	}
	
	/**
	 * Translate current pushed matrix. Shortcut for {@link Matrix4f#translate(float, float, float)}
	 * @param x Translate X parameter
	 * @param y Translate Y parameter
	 * @param z Translate Z parameter
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#translate(float, float, float)
	 */
	public ModelHandler translate(float x, float y, float z) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].translate( x, y, z );
		this.matrix.translate(x, y, z);
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
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].translate( x, 0, 0 );
		this.matrix.translate(x, 0, 0);
		return this;
		
	}
	
	/**
	 * Translate current pushed matrix on y axis
	 * @param y Translate amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#translate(float, float, float)
	 */
	public ModelHandler translateY(float y) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].translate( 0, y, 0 );
		this.matrix.translate(0, y, 0);
		return this;
		
	}
	
	/**
	 * Translate current pushed matrix on z axis
	 * @param z Translate amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#translate(float, float, float)
	 */
	public ModelHandler translateZ(float z) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].translate( 0, 0, z );
		this.matrix.translate(0, 0, z);
		return this;
		
	}
	
	/**
	 * Scale current pushed matrix. Shortcut {@link Matrix4f#scale(float, float, float)}
	 * @param x Scale X parameter
	 * @param y Scale Y parameter
	 * @param z Scale Z parameter
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float, float, float)
	 */
	public ModelHandler scale(float x, float y, float z) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].scale( x, y, z );
		this.matrix.scale(x, y, z);
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
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].scale( x, 1, 1 );
		this.matrix.scale(x, 1, 1);
		return this;
		
	}
	
	/**
	 * Scale current pushed matrix on y axis
	 * @param y Scale amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float, float, float)
	 */
	public ModelHandler scaleY(float y) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].scale( 1, y, 1 );
		this.matrix.scale(1, y, 1);
		return this;
		
	}
	
	/**
	 * Scale current pushed matrix on z axis
	 * @param z Scale amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float, float, float)
	 */
	public ModelHandler scaleZ(float z) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].scale( 1, 1, z );
		this.matrix.scale(1, 1, z);
		return this;
		
	}
	
	/**
	 * Scale current pushed matrix on all (x,y,z) axis
	 * @param xyz Scale amount
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#scale(float)
	 */
	public ModelHandler scale(float xyz) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].scale( xyz );
		this.matrix.scale(xyz);
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
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].rotate( ang, x, y, z );
		this.matrix.rotate(ang, x, y, z);
		return this;
		
	}
	
	/**
	 * Rotate current pushed matrix by angle in radian for x axis
	 * @param ang Rotate angle (in radians)
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#rotateX(float)
	 */
	public ModelHandler rotateX(float ang) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].rotateX( ang );
		this.matrix.rotateX(ang);
		return this;
		
	}
	
	/**
	 * Rotate current pushed matrix by angle in radian for y axis
	 * @param ang Rotate angle (in radians)
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#rotateY(float)
	 */
	public ModelHandler rotateY(float ang) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].rotateY( ang );
		this.matrix.rotateY(ang);
		return this;
		
	}
	
	/**
	 * Rotate current pushed matrix by angle in radian for z axis
	 * @param ang Rotate angle (in radians)
	 * @return This {@link ModelHandler} instance.
	 * @see Matrix4f#rotateZ(float)
	 */
	public ModelHandler rotateZ(float ang) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].rotateZ( ang );
		this.matrix.rotateZ(ang);
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
	 * @deprecated Should not be used, wrong explanation and behaviour.
	 */
	@Deprecated
	public ModelHandler rotateOrigin(float originX, float originY, float originZ, float ang, float x, float y, float z) {
		
		// if ( this.modelsMatrices.length == 0 ) return this;
		// this.modelsMatrices[ this.modelsMatrices.length - 1 ].translate( originX, originY, originZ ).rotate( ang, x, y, z ).translate( -originX, -originY, -originZ );
		this.matrix.translate( originX, originY, originZ ).rotate( ang, x, y, z ).translate( -originX, -originY, -originZ );
		return this;
		
	}
	
	/**
	 * Pop current model matrix. (like {@link GL11#glPopMatrix()})
	 * This function remove (only if the array has at least one matrix) the current matrix from the array.
	 * And (if one matrix remain) using previous matrix.
	 * @return This {@link ModelHandler} instance.
	 */
	public ModelHandler pop() {
		
		/*
		if ( this.modelsMatrices.length == 0 ) return this;
		
		Matrix4f[] newModels = new Matrix4f[ this.modelsMatrices.length - 1 ];
		System.arraycopy( this.modelsMatrices, 0, newModels, 0, newModels.length );
		this.modelsMatrices = newModels;
		
		this.apply();
		*/
		
		if (this.step == 0)
			return this;
		
		if ((--this.step) == 0) {
			this.matrix.identity();
		} else {
			this.matrix.set(this.models, (this.step << 4) - 16);
		}
		
		// this.apply(); // FIXME : Removed this to avoid unused model uploads
		
		return this;
		
	}
	
	/**
	 * @return Current model matrix, or null if no matrix as been initialized.
	 */
	public Matrix4f current() {
		// if ( this.modelsMatrices.length == 0 ) return null;
		// return this.modelsMatrices[ this.modelsMatrices.length - 1 ];
		return this.matrix;
	}
	
	/**
	 * Applying current model matrix (do nothing if no matrix in the array) with {@link ModelApplyListener#modelApply(Matrix4f)}
	 * @return This {@link ModelHandler} instance.
	 */
	public ModelHandler apply() {
		
		// if ( this.modelsMatrices.length == 0 ) this.listener.modelApply( null );
		// else this.listener.modelApply( this.modelsMatrices[ this.modelsMatrices.length - 1 ] );
		this.listener.modelApply(this.matrix);
		return this;
		
	}
	
	/**
	 * Clear this {@link ModelHandler} matrix array.
	 */
	public void reset() {
		
		this.matrix.identity();
		this.step = 0;
		this.models = new float[INITIAL_MODELS_CAPACITY << 4];
		
	}
	
}
