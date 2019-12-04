package fr.theorozier.procgen.renderer.world.layer;

import fr.theorozier.procgen.renderer.world.block.BlockFaces;
import fr.theorozier.procgen.renderer.world.block.BlockRenderer;
import fr.theorozier.procgen.world.chunk.WorldBlock;

public class ChunkCompiledBlock implements Comparable<ChunkCompiledBlock> {

	private final BlockRenderer renderer;
	private final WorldBlock block;
	private final byte faces;
	private final int x, y, z;
	
	private int distanceSquared;
	
	ChunkCompiledBlock(BlockRenderer renderer, WorldBlock block, BlockFaces faces) {
		
		this.renderer = renderer;
		this.block = block;
		this.faces = faces.toByte();
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		
	}
	
	public BlockRenderer getRenderer() {
		return this.renderer;
	}
	
	public WorldBlock getBlock() {
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
