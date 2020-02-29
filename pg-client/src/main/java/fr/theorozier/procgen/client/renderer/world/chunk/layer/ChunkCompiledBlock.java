package fr.theorozier.procgen.client.renderer.world.chunk.layer;

import fr.theorozier.procgen.client.renderer.block.BlockFaces;
import fr.theorozier.procgen.client.renderer.block.BlockRenderer;
import fr.theorozier.procgen.common.block.state.BlockState;

public class ChunkCompiledBlock implements Comparable<ChunkCompiledBlock> {

	private final BlockRenderer renderer;
	private final BlockState block;
	private final byte faces;
	private final int x, y, z;
	
	private int distanceSquared;
	
	ChunkCompiledBlock(BlockRenderer renderer, BlockState block, BlockFaces faces, int x, int y, int z) {
		
		this.renderer = renderer;
		this.block = block;
		this.faces = faces.toByte();
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public BlockRenderer getRenderer() {
		return this.renderer;
	}
	
	public BlockState getBlock() {
		return this.block;
	}
	
	public void mutateBlockFaces(BlockFaces faces) {
		faces.setData(this.faces);
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getZ() {
		return this.z;
	}
	
	public void recomputeDistanceTo(float x, float y, float z) {
		
		float dx = x - this.x;
		float dy = y - this.y;
		float dz = z - this.z;
		
		this.distanceSquared = (int) ((dx * dx + dy * dy + dz * dz) * 100f);
		
	}
	
	@Override
	public int compareTo(ChunkCompiledBlock o) {
		return this.distanceSquared - o.distanceSquared;
	}
	
}
