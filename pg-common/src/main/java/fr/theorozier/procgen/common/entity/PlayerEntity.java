package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;

public class PlayerEntity extends HumanoidEntity {
	
	public PlayerEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.health = 20;
		
	}
	
	@Override
	protected void setupBoundingBoxPosition(double x, double y, double z) {
		this.boundingBox.setPositionUnsafe(x - 0.375f, y, z - 0.375f, x + 0.375f, y + 1.8f, z + 0.375f);
	}
	
	@Override
	public void resetPositionToBoundingBox() {
		
		this.posX = this.boundingBox.getMiddleX();
		this.posY = this.boundingBox.getMinY();
		this.posZ = this.boundingBox.getMiddleZ();
		
	}
	
	@Override
	protected float getWalkFrameSpeed(float sqDist) {
		return Math.min((float) Math.sqrt(sqDist) * 2f, 1f);
	}
}
