package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;

import java.util.Objects;

public class FallingBlockEntity extends MotionEntity {
	
	private BlockState state = Blocks.STONE.getDefaultState();
	
	public FallingBlockEntity(WorldBase world, long uid) {
		super(world, uid);
	}
	
	public BlockState getState() {
		return this.state;
	}
	
	public void setState(BlockState state) {
		this.state = Objects.requireNonNull(state, "Block state can't be null.");
	}
	
	@Override
	protected void setupBoundingBoxPosition(double x, double y, double z) {
		this.boundingBox.setPositionUnsafe(x, y, z, x + 1.0, y + 1.0, z + 1.0);
	}
	
	@Override
	public void resetPositionToBoundingBox() {
		
		this.posX = this.boundingBox.getMinX();
		this.posY = this.boundingBox.getMinY();
		this.posZ = this.boundingBox.getMinZ();
		
	}
	
}
