package fr.theorozier.procgen.block;

import fr.theorozier.procgen.util.ErrorUtils;
import fr.theorozier.procgen.world.Direction;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.sutil.StringUtils;

import java.util.Random;

public class Block {
	
	private final short uid;
	private final String identifier;
	
	public Block(int uid, String identifier) {
		
		if (uid <= 0)
			throw ErrorUtils.invalidUidArgument("Block");
		
		this.uid = (short) uid;
		this.identifier = StringUtils.requireNonNullAndEmpty(identifier, "Block's identifier can't be null or empty.");
		
	}
	
	public short getUid() {
		return this.uid;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public boolean isUnsavable() {
		return false;
	}
	
	public boolean isOpaque() {
		return true;
	}
	
	public boolean mustRenderFace(WorldBlock wb, Direction otherDir, WorldBlock other) {
		
		if (other == null) {
			return true;
		} else {
			
			Block otherBlock = other.getBlockType();
			
			if (this != otherBlock) {
				return !otherBlock.isOpaque();
			} else {
				return !this.isOpaque() && this.mustRenderSameBlockFaces();
			}
			
		}
		
	}
	
	public boolean mustRenderSameBlockFaces() {
		return false;
	}
	
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.OPAQUE;
	}
	
	public boolean isTickable() {
		return true;
	}
	
	// Dynamic function for modifying the world
	
	public void initBlock(World world, WorldBlock block) {}
	
	public void tickBlock(World world, WorldBlock block, Random rand) {}
	
	@Override
	public int hashCode() {
		return this.getUid();
	}
	
	@Override
	public String toString() {
		return "Block{" +
				"uid=" + uid +
				", identifier='" + identifier + '\'' +
				'}';
	}
}
