package fr.theorozier.procgen.client.renderer.world;

import io.msengine.client.renderer.basic.Basic3DShaderManager;
import io.msengine.client.renderer.shader.ShaderUniformBase;
import io.msengine.client.renderer.shader.ShaderValueType;
import io.msengine.common.util.Color;
import org.joml.Matrix4f;

public class WorldShaderManager extends Basic3DShaderManager {
	
	public static final String MODEL_MATRIX  = "model_matrix";
	public static final String GLOBAL_OFFSET = "global_offset";
	
	public static final String GLOBAL_COLOR = "global_color";
	
	public WorldShaderManager() {
		
		super("world", "world", "world");
		
		this.registerUniform(MODEL_MATRIX, ShaderValueType.MAT4);
		this.registerUniform(GLOBAL_OFFSET, ShaderValueType.VEC3);
		this.registerUniform(GLOBAL_COLOR, ShaderValueType.VEC4);
		
	}
	
	public ShaderUniformBase getModelMatrixUniform() {
		return this.getShaderUniformOrDefault(MODEL_MATRIX);
	}
	
	public void setModelMatrix(Matrix4f matrix) {
		this.getModelMatrixUniform().set(matrix);
	}
	
	public ShaderUniformBase getGlobalOffsetUniform() {
		return this.getShaderUniformOrDefault(GLOBAL_OFFSET);
	}
	
	public void setGlobalOffset(float x, float y, float z) {
		this.getGlobalOffsetUniform().set(x, y, z);
	}
	
	public ShaderUniformBase getGlobalColorUniform() {
		return this.getShaderUniformOrDefault(GLOBAL_COLOR);
	}
	
	public void setGlobalColor(Color color) {
		this.getGlobalColorUniform().setRGBA(color);
	}
	
	public void setGlobalColor(float r, float g, float b, float a) {
		this.getGlobalColorUniform().set(r, g, b, a);
	}
	
	public void resetGlobalColor() {
		this.setGlobalColor(Color.WHITE);
	}
	
}
