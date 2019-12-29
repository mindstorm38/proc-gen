package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;

public class MotionEntity extends Entity {
	
	private float velX;
	private float velY;
	private float velZ;
	
	private float lastX;
	private float lastY;
	private float lastZ;
	
	public MotionEntity(WorldBase world, long uid) {
		super(world, uid);
	}
	
	@Override
	public void update() {
		
		super.update();
		
		this.lastX = this.posX;
		this.lastY = this.posY;
		this.lastZ = this.posZ;
		
		this.posX += this.velX;
		this.posY += this.posY;
		this.posZ += this.posZ;
		
	}
	
}
