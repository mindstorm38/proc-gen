package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.entity.animation.EntityFrame;
import fr.theorozier.procgen.common.entity.animation.EntityInterpolationRange;
import fr.theorozier.procgen.common.world.WorldBase;

public abstract class WalkingEntity extends LiveEntity {
	
	protected final EntityFrame walkFrame;
	protected final EntityInterpolationRange walkingIterpolation;
	
	public WalkingEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.walkFrame = new EntityFrame();
		this.walkingIterpolation = new EntityInterpolationRange();
		
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
			
			this.walkFrame.addValue(this.getWalkFrameSpeed((float) (dx * dx + dy * dy + dz * dz)));
			this.walkingIterpolation.increment(0.04f);
			
		} else {
			this.walkFrameBackToZero();
		}
		
	}
	
	@Override
	protected void onIdle() {
		super.onIdle();
		this.walkFrameBackToZero();
	}
	
	protected void walkFrameBackToZero() {
		this.walkFrame.backToZero(0.3f);
		this.walkingIterpolation.decrement(0.04f);
	}
	
	protected float getWalkFrameSpeed(float sqDist) {
		return sqDist;
	}
	
	// PROPERTIES GET & SET //
	
	public EntityFrame getWalkFrame() {
		return walkFrame;
	}
	
	public EntityInterpolationRange getWalkingInterpolation() {
		return this.walkingIterpolation;
	}
	
}
