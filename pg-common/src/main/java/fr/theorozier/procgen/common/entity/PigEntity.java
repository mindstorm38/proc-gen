package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;

public class PigEntity extends WalkingEntity {
	
	public PigEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.health = 10;
		
	}
	
	@Override
	protected void setupBoundingBoxPosition(double x, double y, double z) {
		this.boundingBox.setPositionUnsafe(x - 0.5f, y, z - 0.5f, x + 0.5f, y + 0.6f, z + 0.5f);
	}
	
	@Override
	public void resetPositionToBoundingBox() {
		
		this.posX = this.boundingBox.getMiddleX();
		this.posY = this.boundingBox.getMinY();
		this.posZ = this.boundingBox.getMiddleZ();
		
	}
	
	@Override
	protected float getWalkFrameSpeed(float sqDist) {
		return sqDist * 30f;
	}
}
