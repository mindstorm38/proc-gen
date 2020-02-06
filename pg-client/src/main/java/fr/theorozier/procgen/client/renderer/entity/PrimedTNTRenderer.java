package fr.theorozier.procgen.client.renderer.entity;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.renderer.entity.part.EntityBlockRendererPart;
import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.entity.PrimedTNTEntity;
import io.msengine.client.renderer.model.ModelHandler;
import io.msengine.client.renderer.texture.Texture;
import io.msengine.client.renderer.texture.TextureMap;

public class PrimedTNTRenderer extends MotionEntityRenderer<PrimedTNTEntity> {
	
	private final EntityBlockRendererPart part;
	private TextureMap terrainMap = null;
	
	public PrimedTNTRenderer() {
		
		this.part = new EntityBlockRendererPart(Blocks.TNT.getDefaultState());
		this.addPart("primed_tnt", this.part);
		
	}
	
	@Override
	public void initTexture() {
		this.terrainMap = ProcGenGame.getGameInstance().getWorldRenderer().getTerrainMap();
	}
	
	@Override
	public Texture getTexture(PrimedTNTEntity entity) {
		return this.terrainMap;
	}
	
	@Override
	public void renderMotionEntity(float alpha, ModelHandler model, PrimedTNTEntity entity) {
		
		float flashingVal = (float) Math.sin(entity.getFlashingFrame().getLerped(alpha)) * 0.25f + 0.75f;
		
		this.shaderManager.setGlobalColor(flashingVal, flashingVal, flashingVal, 1f);
		this.part.render();
		this.shaderManager.resetGlobalColor();
		
	}
	
}
