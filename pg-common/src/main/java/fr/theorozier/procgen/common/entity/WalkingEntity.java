package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.entity.animation.EntityFrame;
import fr.theorozier.procgen.common.world.WorldBase;

public abstract class WalkingEntity extends LiveEntity {
	
	protected final EntityFrame walkFrame;
	
	public WalkingEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.walkFrame = new EntityFrame();
		
	}
	
	// BASIC UPDATES //
	
	@Override
	public void update() {
		
		super.update();
		
	}
	
	@Override
	protected void updateMotion() {
		
		this.walkFrame.setLast();
		super.updateMotion();
		
	}
	
	// MOVED EVENTS //
	
	@Override
	protected void onMoved(double dx, double dy, double dz) {
		
		super.onMoved(dx, dy, dz);
		
		if (this.onGround) {
			this.walkFrame.addValue((float) (dx * dx + dy * dy + dz * dz) * 30f);
		} else {
			this.walkFrame.backToZero(0.3f);
		}
		
	}
	
	@Override
	protected void onIdle() {
		this.walkFrame.backToZero(0.3f);
	}
	
	// PROPERTIES GET & SET //
	
	public EntityFrame getWalkFrame() {
		return walkFrame;
	}
	
}
