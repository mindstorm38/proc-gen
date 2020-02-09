package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import io.sutil.pool.FixedObjectPool;

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
			
			try (FixedObjectPool<BlockPosition>.PoolObject poolPos = BlockPosition.POOL.acquire()) {
				
				BlockPosition pos = poolPos.get();
				pos.set((int) Math.round(this.posX), (int) Math.round(this.posY), (int) Math.round(this.posZ));
				
				BlockState state = this.serverWorld.getBlockAt(pos);
				
				if (state == null || state.getBlock().canOverride(this.serverWorld, pos, state)) {
					this.serverWorld.setBlockAt(pos, this.state);
				} else {
					// Drop block item
				}
				
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
