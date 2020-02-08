package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.block.state.BlockStateContainer;
import fr.theorozier.procgen.common.item.ItemStack;
import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.util.ErrorUtils;
import fr.theorozier.procgen.common.world.WorldServer;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;
import io.sutil.StringUtils;

import java.util.List;
import java.util.Random;

public class Block {
	
	protected final short uid;
	protected final String identifier;
	
	protected final BlockStateContainer stateContainer;
	private BlockState defaultState;
	
	private float resistance = 1f;
	
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
	
	public boolean mustRenderFace(BlockState state, Direction otherDir, BlockState other) {
		
		if (other == null) {
			return true;
		} else {
			
			Block otherBlock = other.getBlock();
			
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
	
	public void getStateCollision(BlockState state, List<AxisAlignedBB> boundingBoxes) {
		addCubeBoundingBox(boundingBoxes);
	}
	
	public float getInnerViscosity() {
		return 0f;
	}
	
	// Properties
	
	public float getResistance(BlockState state) {
		return resistance;
	}
	
	public void setResistance(float resistance) {
		this.resistance = resistance;
	}
	
	// Dynamic function for modifying the world
	
	public void tickBlock(WorldServer world, BlockPositioned pos, BlockState block, Random rand) {}

	public void placedBlock(WorldServer world, BlockPositioned pos, BlockState block) {}

	public void destroyedBlock(WorldServer world, BlockPositioned pos, BlockState block, ItemStack usedItem) {}

	public void interactBlock(WorldServer world, BlockPositioned pos, BlockState block, Direction face, ItemStack usedItem) {}

	public void neighbourBlockUpdated(WorldServer world, BlockPositioned pos, BlockState block, BlockPositioned neighbourPos, BlockState neighbour, Direction face) {}

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
	
	// Utilities for blocks bounding boxes
	public static void addCubeBoundingBox(List<AxisAlignedBB> boundingBoxes) {
		boundingBoxes.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1));
	}
	
}
