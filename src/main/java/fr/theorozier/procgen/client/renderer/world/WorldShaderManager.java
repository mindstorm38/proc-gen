package fr.theorozier.procgen.client.renderer.world;

import io.msengine.client.renderer.basic.Basic3DShaderManager;
import io.msengine.client.renderer.shader.ShaderUniformBase;
import io.msengine.client.renderer.shader.ShaderValueType;

public class WorldShaderManager extends Basic3DShaderManager {
	
	public static final String GLOBAL_OFFSET = "global_offset";
	
	public WorldShaderManager() {
		
		super("world", "world", "world");
		
		this.registerUniform(GLOBAL_OFFSET, ShaderValueType.VEC2);
		
	}
	
	public ShaderUniformBase getGlobalOffsetUniform() {
		return this.getShaderUniformOrDefault(GLOBAL_OFFSET);
	}
	
	public void setGlobalOffset(float x, float z) {
		this.getGlobalOffsetUniform().set(x, z);
	}
	
}
