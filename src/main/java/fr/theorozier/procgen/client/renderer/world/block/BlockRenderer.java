package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.common.util.array.BufferedFloatArray;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.common.util.Color;

public abstract class BlockRenderer {
	
	public abstract void getRenderData(WorldBlock block, float x, float y, float z, BlockFaces face, TextureMap map, WorldRenderDataArray dataArray);
	
	protected static void addWhiteColor(BufferedFloatArray colors, int c) {
		for (int i = 0; i < c; ++i) colors.put(1).put(1).put(1);
	}
	
	protected static void addColor(BufferedFloatArray colors, Color color, int c) {
		for (int i = 0; i < c; ++i) colors.put(color.getRed()).put(color.getGreen()).put(color.getBlue());
	}
	
	protected static void addColor(BufferedFloatArray colors, Color color) {
		colors.put(color.getRed()).put(color.getGreen()).put(color.getBlue());
	}
	
	protected static int posRand(float x, float y, float z) {
		return (int) (Math.sin(x * 12.9898f + y * 53.5014f + z * 78.233f) * 43758.5453123f);
	}
	
}
