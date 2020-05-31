package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.client.renderer.buffer.WorldRenderBuffer;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldAccessor;
import fr.theorozier.procgen.common.world.biome.Biome;
import io.msengine.client.renderer.texture.TextureMap;
import io.msengine.common.util.Color;

public abstract class BlockRenderer {
	
	public static final float OCCLUSION_FACTOR = 0.8f;
	
	protected static int posRand(float x, float y, float z) {
		return (int) (Math.sin(x * 12.9898f + y * 53.5014f + z * 78.233f) * 43758.5453123f);
	}
	
	private static boolean isBlockOpaque(BlockState state) {
		return state != null && state.isBlockOpaque();
	}
	
	/**
	 *  <pre>
	 *
	 *         Y
	 *        4+---------+5
	 *       / |       / |
	 *    6+---------+7  |
	 *     |   |     |   |
	 *     |  0+-----|---+1 X
	 *     | /       | /
	 *    2+---------+3
	 *     Z
	 *
	 *  </pre>
	 *
	 *  Return an integer representing a sequence of 4 bits describing ambient occlusion for each face (total of 24 bits)
	 *  in this order :<br>
	 *  <b>0b</b> WEST EAST SOUTH NORTH BOTTOM TOP<br>
	 *  For each face, bits are puts from most significant bit (the origin corner, 0, or else the only corner
	 *  with only one coordinate set to one, like 1,0,0) to low significant bit in anti-clockwise corner order.
	 *
	 * @param world The world used to get opaque blocks informations.
	 * @param x Block X coord.
	 * @param y Block Y coord.
	 * @param z Block Z coord.
	 * @param faces Faces data used to optimize results (currently not used).
	 * @return An integer used to store bits of informations per corners per faces.
	 */
	protected static int computeAmbientOcclusion(WorldAccessor world, int x, int y, int z, BlockFaces faces) {
		
		// X/Y Plane
		int r = isBlockOpaque(world.getBlockAt(x - 1, y + 1, z)) ? 0b0000_0000_0011_0000_0000_1100 : 0;
		
		if (isBlockOpaque(world.getBlockAt(x + 1, y + 1, z)))
			r |= 0b0000_0000_0000_0110_0000_0011;
		
		if (isBlockOpaque(world.getBlockAt(x - 1, y - 1, z)))
			r |= 0b0000_0000_1100_0000_1001_0000;
		
		if (isBlockOpaque(world.getBlockAt(x + 1, y - 1, z)))
			r |= 0b0000_0000_0000_1001_0110_0000;
		
		// Z/Y Plane
		if (isBlockOpaque(world.getBlockAt(x, y + 1, z - 1)))
			r |= 0b0110_0000_0000_0000_0000_1001;
		
		if (isBlockOpaque(world.getBlockAt(x, y + 1, z + 1)))
			r |= 0b0000_0011_0000_0000_0000_0110;
		
		if (isBlockOpaque(world.getBlockAt(x, y - 1, z - 1)))
			r |= 0b1001_0000_0000_0000_1100_0000;
		
		if (isBlockOpaque(world.getBlockAt(x, y - 1, z + 1)))
			r |= 0b0000_1100_0000_0000_0011_0000;
		
		// X/Z Plane
		if (isBlockOpaque(world.getBlockAt(x - 1, y, z - 1)))
			r |= 0b1100_0000_1001_0000_0000_0000;
		
		if (isBlockOpaque(world.getBlockAt(x + 1, y, z - 1)))
			r |= 0b0011_0000_0000_1100_0000_0000;
		
		if (isBlockOpaque(world.getBlockAt(x + 1, y, z + 1)))
			r |= 0b0000_0110_0000_0011_0000_0000;
		
		if (isBlockOpaque(world.getBlockAt(x - 1, y, z + 1)))
			r |= 0b0000_1001_0110_0000_0000_0000;
		
		// Additionnal corners Y-1
		if ((r & 8421504) != 8421504 && isBlockOpaque(world.getBlockAt(x - 1, y - 1, z - 1)))
			r |= 8421504;
		
		if ((r & 1050688) != 1050688 && isBlockOpaque(world.getBlockAt(x + 1, y - 1, z - 1)))
			r |= 1050688;
		
		if ((r & 262432) != 262432 && isBlockOpaque(world.getBlockAt(x + 1, y - 1, z + 1)))
			r |= 262432;
		
		if ((r & 540688) != 540688 && isBlockOpaque(world.getBlockAt(x - 1, y - 1, z + 1)))
			r |= 540688;
		
		// Additionnal corners Y+1
		if ((r & 4198408) != 4198408 && isBlockOpaque(world.getBlockAt(x - 1, y + 1, z - 1)))
			r |= 4198408;
		
		if ((r & 2098177) != 2098177 && isBlockOpaque(world.getBlockAt(x + 1, y + 1, z - 1)))
			r |= 2098177;
		
		if ((r & 131586) != 131586 && isBlockOpaque(world.getBlockAt(x + 1, y + 1, z + 1)))
			r |= 131586;
		
		if ((r & 73732) != 73732 && isBlockOpaque(world.getBlockAt(x - 1, y + 1, z + 1)))
			r |= 73732;
		
		return r;
		
	}
	
	public abstract void getRenderData(WorldAccessor world, BlockState block, int bx, int by, int bz, float x, float y, float z, BlockFaces faces, TextureMap map, WorldRenderBuffer dataArray);
	
	public boolean needFaces() {
		return true;
	}
	
	protected static Color getBlockColor(WorldAccessor world, int x, int y, int z, BlockColorResolver resolver) {
		
		final int blendingRadius = 4;
		
		Biome biome;
		Color color;
		
		float r = 0;
		float g = 0;
		float b = 0;
		
		int xMax = x + blendingRadius;
		int zMax = z + blendingRadius;
		
		for (int bx = x - blendingRadius; bx <= xMax; ++bx) {
			for (int bz = z - blendingRadius; bz <= zMax; ++bz) {
				if ((biome = world.getBiomeAt(bx, bz)) != null) { // Null should not happen
					
					color = resolver.getColor(biome);
					r += color.getRed();
					g += color.getGreen();
					b += color.getBlue();
					
				}
			}
		}
		
		int total = (blendingRadius * 2 + 1) * (blendingRadius * 2 + 1);
		
		return new Color(r / total, g / total, b / total);
		
	}
	
}
