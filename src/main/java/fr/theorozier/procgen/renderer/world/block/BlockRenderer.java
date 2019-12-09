package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.util.array.BufferedFloatArray;
import fr.theorozier.procgen.util.array.BufferedIntArray;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.msengine.client.renderer.texture.TextureMap;

public abstract class BlockRenderer {
	
	public abstract int getRenderData(WorldBlock block, float x, float y, float z, int idx, BlockFaces faces, TextureMap map, BufferedFloatArray vertices, BufferedFloatArray colors, BufferedIntArray indices, BufferedFloatArray texcoords);
	
}
