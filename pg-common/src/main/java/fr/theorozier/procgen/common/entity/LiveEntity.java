package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.world.WorldBase;
import io.sutil.math.MathHelper;

public abstract class LiveEntity extends MotionEntity {
	
	protected float headYaw;
	protected float headPitch;
	
	protected float lastHeadYaw;
	protected float lastHeadPitch;
	
	protected int health;
	protected float fallResistance;
	
	public LiveEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.health = 1;
		this.fallResistance = 3f;
		
	}
	
	// BASIC UPDATES //
	
	@Override
	public void update() {
		
		super.update();
		
		this.updateHealth();
		
	}
	
	@Override
	protected void updateMotion() {
		
		this.setLastHeadRotation();
		super.updateMotion();
		
	}
	
	// HEALTH MANAGEMENT //
	
	public void updateHealth() {
		
		if (this.health <= 0)
			this.setDead();
		
	}
	
	public void damage(DamageSource source, float damage) {
		this.health -= MathHelper.floorFloatInt(damage);
	}
	
	// FALLEN MOTION EVENTS //
	
	@Override
	public void onFallen(double distance) {
		
		float damage = (float) (distance - this.fallResistance);
		
		if (damage > 0) {
			this.damage(DamageSource.FALL, damage / this.fallResistance);
		}
		
	}
	
	// HEAD ROTATION //
	
	/**
	 * Set the head rotation.
	 * @param yaw Yaw rotation of the head.
	 * @param pitch Pitch rotation of the head.
	 */
	public void setHeadRotation(float yaw, float pitch) {
		
		this.headYaw = yaw;
		this.headPitch = pitch;
		
	}
	
	public void setLastHeadRotation() {
		
		this.lastHeadYaw = this.headYaw;
		this.lastHeadPitch = this.headPitch;
		
	}
	
	// LINEAR INTERPOLATION METHODS //
	
	public float getLerpedHeadYaw(float alpha) {
		return MathHelper.interpolate(alpha, this.headYaw, this.lastHeadYaw);
	}
	
	public float getLerpedHeadPitch(float alpha) {
		return MathHelper.interpolate(alpha, this.headPitch, this.lastHeadPitch);
	}
	
	// PROPERTIES GET & SET //
	
	public float getHeadYaw() {
		return headYaw;
	}
	
	public float getHeadPitch() {
		return headPitch;
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
}
