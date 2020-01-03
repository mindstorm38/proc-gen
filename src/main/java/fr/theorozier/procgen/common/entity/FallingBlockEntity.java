package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.WorldServer;

public class FallingBlockEntity extends MotionEntity {
	
	public FallingBlockEntity(WorldBase world, WorldServer serverWorld, long uid) {
		super(world, serverWorld, uid);
	}
	
	@Override
	public void setPositionInstant(double x, double y, double z) {
		
		super.setPositionInstant(x, y, z);
		this.boundingBox.setPositionUnsafe(x, y, z, x + 1.0, y + 1.0, z + 1.0);
		
	}
	
	@Override
	public void resetPositionToBoundingBox() {
		
		this.posX = this.boundingBox.getMinX();
		this.posY = this.boundingBox.getMinY();
		this.posZ = this.boundingBox.getMinZ();
		
	}
	
}
