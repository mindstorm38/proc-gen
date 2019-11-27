package fr.theorozier.procgen.world;

import fr.theorozier.procgen.block.Block;
import io.msengine.common.osf.OSFObject;

public class WorldBlock {

	private final WorldChunk chunk;
	private final WorldBlockPosition position;
	private final WorldBlockPosition relative;
	private final short positionIndex;
	
	WorldBlock(WorldChunk chunk, WorldBlockPosition position, WorldBlockPosition relative) {
		
		this.chunk = chunk;
		this.position = position;
		this.relative = relative;
		this.positionIndex = chunk.getPositionIndex(position.getX(), position.getY(), position.getZ());
		
	}
	
	public World getWorld() {
		return this.chunk.getWorld();
	}
	
	public WorldChunk getChunk() {
		return this.chunk;
	}
	
	public WorldBlockPosition getPosition() {
		return this.position;
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
