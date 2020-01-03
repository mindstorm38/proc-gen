package fr.theorozier.procgen.client.renderer.world;

import io.msengine.client.renderer.basic.Basic3DShaderManager;
import io.msengine.client.renderer.shader.ShaderUniformBase;
import io.msengine.client.renderer.shader.ShaderValueType;
import org.joml.Matrix4f;

public class WorldShaderManager extends Basic3DShaderManager {
	
	public static final String MODEL_MATRIX  = "model_matrix";
	public static final String GLOBAL_OFFSET = "global_offset";
	
	public WorldShaderManager() {
		
		super("world", "world", "world");
		
		this.registerUniform(MODEL_MATRIX, ShaderValueType.MAT4);
		this.registerUniform(GLOBAL_OFFSET, ShaderValueType.VEC3);
		
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
	
}
