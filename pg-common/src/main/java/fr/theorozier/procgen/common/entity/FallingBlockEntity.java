package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;

public class FallingBlockEntity extends MotionEntity {
	
	public FallingBlockEntity(WorldBase world, long uid) {
		super(world, uid);
	}
	
	@Override
	public void setupBoundingBoxPosition(double x, double y, double z) {
		this.boundingBox.setPositionUnsafe(x, y, z, x + 1.0, y + 1.0, z + 1.0);
	}
	
	@Override
	public void resetPositionToBoundingBox() {
		
		this.posX = this.boundingBox.getMinX();
		this.posY = this.boundingBox.getMinY();
		this.posZ = this.boundingBox.getMinZ();
		
	}
	
}
