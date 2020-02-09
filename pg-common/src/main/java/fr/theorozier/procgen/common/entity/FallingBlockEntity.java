package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;

import java.util.Objects;

public class FallingBlockEntity extends MotionEntity {
	
	protected BlockState state = Blocks.STONE.getDefaultState();
	protected boolean placeOnIdle = true;
	
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
	
	// MOTION EVENTS //
	
	@Override
	protected void onIdle() {
		
		super.onIdle();
		
		if (this.isInServer && this.placeOnIdle) {
			
			int placeX = (int) Math.round(this.posX);
			int placeY = (int) Math.round(this.posY);
			int placeZ = (int) Math.round(this.posZ);
			
			BlockState state = this.serverWorld.getBlockAt(placeX, placeY, placeZ);
			
			if (state == null) {
				this.serverWorld.setBlockAt(placeX, placeY, placeZ, this.state);
			} else {
				// Drop block item
			}
			
			this.setDead();
			
		}
		
	}
	
	// PROPERTIES GET & SET //
	
	public boolean doPlaceOnIdle() {
		return this.placeOnIdle;
	}
	
	public void setPlaceOnIdle(boolean placeOnIdle) {
		this.placeOnIdle = placeOnIdle;
	}
	
}
