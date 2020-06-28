package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.renderer.buffer.WorldRenderBuffer;
import fr.theorozier.procgen.client.renderer.entity.part.EntityBlockRendererPart;
import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.FallingBlockEntity;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.Texture;
import io.msengine.client.renderer.texture.TextureMap;

import java.util.HashMap;
import java.util.Map;

public class FallingBlockEntityRenderer extends MotionEntityRenderer<FallingBlockEntity> {
	
	private final Map<BlockState, EntityBlockRendererPart> blockStateRenderers = new HashMap<>();
	private TextureMap terrainMap = null;
	
	public FallingBlockEntityRenderer() { }
	
	@Override
	public void initRenderer(WorldShaderManager shaderManager, WorldRenderBuffer renderBuffer) {
		super.initRenderer(shaderManager, renderBuffer);
	}
	
	@Override
	public void initTexture() {
		this.terrainMap = ProcGenGame.getGameInstance().getWorldRenderer().getTerrainMap();
	}
	
	@Override
	public Texture getTexture(FallingBlockEntity entity) {
		return this.terrainMap;
	}
	
	public EntityBlockRendererPart getRendererPart(BlockState state) {
		
		return this.blockStateRenderers.computeIfAbsent(state, st -> {
			
			EntityBlockRendererPart part = new EntityBlockRendererPart(st);
			this.addPart(st.repr(), part);
			return part;
			
		});
		
	}
	
	@Override
	public void renderMotionEntity(float alpha, ModelHandler model, FallingBlockEntity entity) {
		
		EntityBlockRendererPart part = this.getRendererPart(entity.getState());
		part.render();
		
	}
	
}
