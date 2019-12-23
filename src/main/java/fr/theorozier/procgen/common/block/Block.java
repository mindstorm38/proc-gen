package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.block.state.BlockStateContainer;
import fr.theorozier.procgen.common.util.ErrorUtils;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.world.util.Direction;
import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import io.sutil.StringUtils;

import java.util.Random;

public class Block {
	
	protected final short uid;
	protected final String identifier;
	
	protected final BlockStateContainer stateContainer;
	private BlockState defaultState;
	
	public Block(int uid, String identifier) {
		
		if (uid <= 0)
			throw ErrorUtils.invalidUidArgument("Block");
		
		this.uid = (short) uid;
		this.identifier = StringUtils.requireNonNullAndEmpty(identifier, "Block's identifier can't be null or empty.");
		
		BlockStateContainer.Builder containerBuilder = new BlockStateContainer.Builder(this);
		this.registerStateContainerProperties(containerBuilder);
		this.stateContainer = containerBuilder.build();
		
		this.setDefaultState(this.stateContainer.getBaseState());
		
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
	
	public void registerStateContainerProperties(BlockStateContainer.Builder builder) {}
	
	public void setDefaultState(BlockState defaultState) {
		this.defaultState = defaultState;
	}
	
	public final BlockState getDefaultState() {
		return this.defaultState;
	}
	
	public final BlockStateContainer getStateContainer() {
		return this.stateContainer;
	}
	
	// Dynamic function for modifying the world
	
	public void initBlock(WorldServer world, BlockPositioned pos, BlockState block) {}
	
	public void tickBlock(WorldServer world, BlockPositioned pos, BlockState block, Random rand) {}
	
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
