package io.msengine.client.graphics.gui.render;

import org.joml.Matrix4f;

public interface GuiStdProgram {
	
	/**
	 * Set the projection matrix for the vertex transformation.
	 * If the program is in use, the matrix must also be uploaded.
	 * @param mat The projection matrix.
	 */
	void setProjectionMatrix(Matrix4f mat);
	
	/**
	 * Force upload the projection matrix if it was changed.
	 * <i>This method does not exists for the model matrix because
	 * this one is updated at least one time per render cycle.</i>
	 */
	void uploadProjectionMatrix();
	
	/**
	 * Set and upload <i>(if the program is currently used)</i> the model matrix
	 * for the vertex transformation.
	 * @param mat The model matrix.
	 */
	void setModelMatrix(Matrix4f mat);

}
