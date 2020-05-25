package fr.theorozier.procgen.client.renderer.entity.part;

import fr.theorozier.procgen.client.ProcGenGame;
import fr.theorozier.procgen.client.renderer.block.BlockFaces;
import fr.theorozier.procgen.client.renderer.block.BlockRenderer;
import fr.theorozier.procgen.client.renderer.block.BlockRenderers;
import fr.theorozier.procgen.client.renderer.world.util.buffer.WorldRenderDataArray;
import fr.theorozier.procgen.client.renderer.world.util.WorldShaderManager;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.biome.Biomes;
import fr.theorozier.procgen.common.world.util.DummyWorld;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.client.renderer.vertex.IndicesDrawBuffer;

public class EntityBlockRendererPart extends EntityModelPart {
	
	private static final DummyWorld DUMMY_EMPTY_WORLD = new DummyWorld(Biomes.PLAIN);
	
	private final BlockState blockState;
	private IndicesDrawBuffer buffer;
	
	public EntityBlockRendererPart(BlockState blockState) {
		
		this.blockState = blockState;
		
	}
	
	@Override
	public void init(WorldShaderManager shaderManager, WorldRenderDataArray dataArray) {
		
		BlockRenderer renderer = BlockRenderers.getRenderer(this.blockState.getBlock());
		
		if (renderer == null)
			return;
		
		TextureMap terrainMap = ProcGenGame.getGameInstance().getWorldRenderer().getTerrainMap();
		
		if (terrainMap == null)
			return;
		
		renderer.getRenderData(DUMMY_EMPTY_WORLD, this.blockState, 0, 0, 0, 0f, 0f, 0f, BlockFaces.ImmutableBlockFaces.FULL_FACES, terrainMap, dataArray);
		
		this.buffer = shaderManager.createBasicDrawBuffer(true, true);
		dataArray.uploadToDrawBuffer(this.buffer);
		
	}
	
	@Override
	public void stop() {
		
		this.buffer.delete();
		this.buffer = null;
		
	}
	
	@Override
	public void render() {
		this.buffer.drawElements();
	}
	
}
