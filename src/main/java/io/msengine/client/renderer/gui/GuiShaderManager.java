package io.msengine.client.renderer.gui;

import org.joml.Matrix4f;

import io.msengine.client.renderer.shader.ShaderManager;
import io.msengine.client.renderer.shader.ShaderSamplerObject;
import io.msengine.client.renderer.shader.ShaderUniformBase;
import io.msengine.client.renderer.shader.ShaderValueType;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;
import io.msengine.common.util.Color;
import io.sutil.CollectionUtils;

import static io.msengine.client.renderer.vertex.VertexElement.*;
import static io.msengine.client.renderer.vertex.type.GuiFormat.*;
import static org.lwjgl.opengl.GL20.*;

public class GuiShaderManager extends ShaderManager {

	public static final String GUI_TEXTURE_SAMPLER	= "texture_sampler";
	public static final String GUI_GLOBAL_MATRIX	= "global_matrix";
	public static final String GUI_GLOBAL_COLOR		= "global_color";
	public static final String GUI_TEXTURE_ENABLED	= "texture_enabled";
	
	public GuiShaderManager() {
		
		super( "gui" );
		
		this.registerAttribute( POSITION_2F );
		this.registerAttribute( COLOR_4F );
		this.registerAttribute( TEX_COORD_2F );
		
		this.registerSampler( GUI_TEXTURE_SAMPLER );
		
		this.registerUniform( GUI_GLOBAL_MATRIX, ShaderValueType.MAT4 );
		this.registerUniform( GUI_GLOBAL_COLOR, ShaderValueType.VEC4 );
		this.registerUniform( GUI_TEXTURE_ENABLED, ShaderValueType.INT );
		
	}
	
	@Override
	public void build() {
		
		super.build();
		
		// Setting default values if color or tex coord are disabled
		this.getShaderAttributeLocationSafe( COLOR_4F, location -> glVertexAttrib4f( location, 1.0f, 1.0f, 1.0f, 1.0f ) );
		this.getShaderAttributeLocationSafe( TEX_COORD_2F, location -> glVertexAttrib2f( location, 0.0f, 0.0f ) );
		
	}
	
	public void setTextureSampler(ShaderSamplerObject sampler) {
		
		this.setSamplerObject( GUI_TEXTURE_SAMPLER, sampler );
		this.getShaderUniformOrDefault( GUI_TEXTURE_ENABLED ).set( sampler == null ? 0 : 1 );
		
	}
	
	public ShaderUniformBase getGlobalMatrixUniform() {
		return this.getShaderUniformOrDefault( GUI_GLOBAL_MATRIX );
	}
	
	public void setGlobalMatrix(Matrix4f mat) {
		this.getGlobalMatrixUniform().set( mat );
	}
	
	public ShaderUniformBase getGlobalColorUniform() {
		return this.getShaderUniformOrDefault( GUI_GLOBAL_COLOR );
	}
	
	public void setGlobalColor(Color color) {
		this.getGlobalColorUniform().setRGBA( color );
	}
	
	public IndicesDrawBuffer createGuiDrawBuffer(boolean color, boolean texture) {
		return new IndicesDrawBuffer( this, GUI, CollectionUtils.arrayStringConditional( new String[] { GUI_POSITION, GUI_COLOR, GUI_TEX_COORD }, new boolean[] { true, color, texture } ) );
	}

}
