package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.renderer.world.ColorMapManager;
import fr.theorozier.procgen.util.array.BufferedFloatArray;
import fr.theorozier.procgen.util.array.BufferedIntArray;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.common.util.Color;

public abstract class BlockRenderer {
	
	public abstract int getRenderData(WorldBlock block, float x, float y, float z, int idx, BlockFaces faces, TextureMap map, ColorMapManager colorMap, BufferedFloatArray colors, BufferedIntArray indices, BufferedFloatArray texcoords, BufferedFloatArray vertices);
	
	protected static void addWhiteColor(BufferedFloatArray colors, int c) {
		for (int i = 0; i < c; ++i) colors.put(1).put(1).put(1);
	}
	
	protected static void addColor(BufferedFloatArray colors, Color color, int c) {
		for (int i = 0; i < c; ++i) colors.put(color.getRed()).put(color.getGreen()).put(color.getBlue());
	}
	
	protected static void addColor(BufferedFloatArray colors, Color color) {
		colors.put(color.getRed()).put(color.getGreen()).put(color.getBlue());
	}
	
}
