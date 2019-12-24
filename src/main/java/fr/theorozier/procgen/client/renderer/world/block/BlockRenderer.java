package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import io.msengine.client.renderer.texture.TextureMap;

public abstract class BlockRenderer {
	
	protected static int posRand(float x, float y, float z) {
		return (int) (Math.sin(x * 12.9898f + y * 53.5014f + z * 78.233f) * 43758.5453123f);
	}
	
	protected static boolean hasOpaqueBlockAt(WorldBase world, int x, int y, int z) {
		BlockState block = world.getBlockAt(x, y, z);
		return block != null && block.getBlock().isOpaque();
	}
	
	public abstract void getRenderData(WorldBase world, BlockState block, float x, float y, float z, BlockFaces face, TextureMap map, WorldRenderDataArray dataArray);
	
	public boolean needFaces() {
		return true;
	}
	
}
