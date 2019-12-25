package fr.theorozier.procgen.client.renderer.world.block;

import fr.theorozier.procgen.client.renderer.world.WorldRenderDataArray;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import io.msengine.client.renderer.texture.TextureMap;

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
	protected static int computeAmbientOcclusion(WorldBase world, int x, int y, int z, BlockFaces faces) {
		
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
	
	/**
	 * Compute all opaque blocks in Y+1 and Y-1 layers. Used for ambient occlusion.
	 * @param world The world used to get blocks data.
	 * @param x Center X coord.
	 * @param y Center Y coord.
	 * @param z Center Z coord.
	 * @return Occlusion data for all 8 corners. Bits 0b11110000 are for bottom corners, 0b00001111 are for top corners.
	 * @see #computeHorizontalAmbientOcclusion(WorldBase, int, int, int)
	 */
	protected static byte computeAmbientOcclusion(WorldBase world, int x, int y, int z) {
		
		byte b = (byte) (computeHorizontalAmbientOcclusion(world, x, y + 1, z) | (computeHorizontalAmbientOcclusion(world, x, y - 1, z) << 4));
		
		
		
		/*
		if ((b & 0b00010001) != 0b00010001 && isBlockOpaque(world.getBlockAt(x - 1, y, z - 1)))
			b |= 0b00010001;
		
		if ((b & 0b00100010) != 0b00100010 && isBlockOpaque(world.getBlockAt(x + 1, y, z - 1)))
			b |= 0b00100010;
		
		if ((b & 0b01000100) != 0b01000100 && isBlockOpaque(world.getBlockAt(x + 1, y, z + 1)))
			b |= 0b01000100;
		
		if ((b & 0b10001000) != 0b10001000 && isBlockOpaque(world.getBlockAt(x - 1, y, z + 1)))
			b |= 0b10001000;
		*/
		
		return b;
		
	}
	
	/**
	 * Compute a Y-layer of opaque blocks.<br>
	 * <table>
	 *     <tr>
	 *         <th>Z\X</th>
	 *         <th>-1</th>
	 *         <th>0</th>
	 *         <th>1</th>
	 *     </tr>
	 *     <tr>
	 *         <th>-1</th>
	 *         <td>0001</td>
	 *         <td>0011</td>
	 *         <td>0010</td>
	 *     </tr>
	 *     <tr>
	 *         <th>0</th>
	 *         <td>1001</td>
	 *         <td></td>
	 *         <td>0110</td>
	 *     </tr>
	 *     <tr>
	 *         <th>1</th>
	 *         <td>1000</td>
	 *         <td>1100</td>
	 *         <td>0100</td>
	 *     </tr>
	 * </table>
	 * @param world The world used to get blocks data.
	 * @param x Center X coord.
	 * @param y Center Y coord.
	 * @param z Center Z coord.
	 * @return Ambient occlusion data for this Y-layer. Only first 4 bits are used as follow to describe corners to occlude : <br>
	 *     0b0001 for (-1,-1) | 0b0010 for (+1,-1) | 0b0100 for (+1,+1) | 0b1000 for (-1,+1)
	 */
	protected static byte computeHorizontalAmbientOcclusion(WorldBase world, int x, int y, int z) {
		
		byte r = 0;
		
		if (isBlockOpaque(world.getBlockAt(x, y, z - 1)))
			r |= 0b0011;
		
		if (isBlockOpaque(world.getBlockAt(x, y, z + 1)))
			r |= 0b1100;
		
		if (r == 0b1111)
			return r;
		
		if (isBlockOpaque(world.getBlockAt(x - 1, y, z)))
			r |= 0b1001;
		
		if (isBlockOpaque(world.getBlockAt(x + 1, y, z)))
			r |= 0b0110;
		
		if (r == 0b1111)
			return r;
		
		if ((r & 0b0001) == 0 && isBlockOpaque(world.getBlockAt(x - 1, y, z - 1)))
			r |= 0b0001;
		
		if ((r & 0b0010) == 0 && isBlockOpaque(world.getBlockAt(x + 1, y, z - 1)))
			r |= 0b0010;
		
		if ((r & 0b0100) == 0 && isBlockOpaque(world.getBlockAt(x + 1, y, z + 1)))
			r |= 0b0100;
		
		if ((r & 0b1000) == 0 && isBlockOpaque(world.getBlockAt(x - 1, y, z + 1)))
			r |= 0b1000;
		
		return r;
		
	}
	
	public abstract void getRenderData(WorldBase world, BlockState block, float x, float y, float z, BlockFaces face, TextureMap map, WorldRenderDataArray dataArray);
	
	public boolean needFaces() {
		return true;
	}
	
}
