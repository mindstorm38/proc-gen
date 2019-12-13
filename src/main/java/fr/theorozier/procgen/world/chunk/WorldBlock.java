package fr.theorozier.procgen.world.chunk;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.BlockRenderLayer;
import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.biome.Biome;
import io.msengine.common.osf.OSFObject;

public class WorldBlock {

	private final Chunk chunk;
	private final BlockPosition position;
	private final BlockPosition relative;
	private final short positionIndex;
	
	WorldBlock(Chunk chunk, BlockPosition position, BlockPosition relative) {
		
		this.chunk = chunk;
		this.position = position;
		this.relative = relative;
		this.positionIndex = Chunk.getPositionIndex(position.getX(), position.getY(), position.getZ());
		
	}
	
	public World getWorld() {
		return this.chunk.getWorld();
	}
	
	public Chunk getChunk() {
		return this.chunk;
	}
	
	public BlockPosition getPosition() {
		return this.position;
	}
	
	public Biome getBiome() {
		return this.chunk.getBiomeAt(this.position);
	}
	
	public int getX() {
		return this.position.getX();
	}
	
	public int getY() {
		return this.position.getY();
	}
	
	public int getZ() {
		return this.position.getZ();
	}
	
	public boolean isInRenderLayer(BlockRenderLayer layer) {
		return this.getBlockType().getRenderLayer() == layer;
	}
	
	public Block getBlockType() {
		return this.chunk.getBlockTypeAtRelative(this.relative.getX(), this.relative.getY(), this.relative.getZ());
	}
	
	public void setBlockType(Block block, boolean overwrite) {
		
		this.chunk.setBlockTypeAtRelative(this.relative.getX(), this.relative.getY(), this.relative.getZ(), block, overwrite);
		this.notifyUpdate(block);
		
	}
	
	public void setBlockType(Block block) {
		this.setBlockType(block, true);
	}
	
	public boolean isSet() {
		return this.chunk.hasBlockAtRelative(this.relative.getX(), this.relative.getY(), this.relative.getZ());
	}
	
	public OSFObject getMetadata() {
		return this.chunk.getBlockMetadataAt(this.positionIndex, true);
	}
	
	private void notifyUpdate(Block block) {
		this.chunk.triggerUpdatedListenersAt(this.position.getX(), this.position.getY(), this.position.getZ(), block);
	}
	
	public void notifyUpdate() {
		this.notifyUpdate(this.getBlockType());
	}

}
