package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;
import io.sutil.math.MathHelper;

public abstract class WalkingEntity extends LiveEntity {
	
	protected float walkFrame;
	protected float lastWalkFrame;
	
	public WalkingEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
	}
	
	// BASIC UPDATES //
	
	@Override
	public void update() {
		
		super.update();
		
	}
	
	@Override
	protected void updateMotion() {
		
		this.setLastWalkFrame();
		super.updateMotion();
		
	}
	
	public void setLastWalkFrame() {
		this.lastWalkFrame = this.walkFrame;
	}
	
	// MOVED EVENT //
	
	@Override
	protected void onMoved(double dx, double dy, double dz) {
		
		super.onMoved(dx, dy, dz);
		
		if (this.onGround) {
		
			double distSquared = dx * dy * dz;
			
			this.walkFrame += distSquared;
			
			if (this.walkFrame >= MathHelper.PI_TWICE)
				this.walkFrame -= MathHelper.PI_TWICE;
			
		} else {
			this.walkFrame = 0;
		}
		
	}
	
	// LINEAR INTERPOLATION METHODS //
	
	public float getLerpedWalkFrame(float alpha) {
		return MathHelper.interpolate(alpha, this.walkFrame, this.lastWalkFrame);
	}
	
	// PROPERTIES GET & SET //
	
	public float getWalkFrame() {
		return walkFrame;
	}
	
}
