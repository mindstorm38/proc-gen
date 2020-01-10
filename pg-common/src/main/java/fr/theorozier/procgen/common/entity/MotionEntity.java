package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.WorldBase;
import io.sutil.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class MotionEntity extends Entity {
	
	protected boolean noClip;
	protected boolean hasMass;
	protected boolean onGround;
	protected double stepHeight;
	
	protected double velX;
	protected double velY;
	protected double velZ;
	
	protected double lastX;
	protected double lastY;
	protected double lastZ;
	
	protected float lastYaw;
	protected float lastPitch;
	
	protected double fallDistance;
	
	private final AxisAlignedBB tempMotionBoundingBox;
	
	public MotionEntity(WorldBase world, long uid) {
		
		super(world, uid);
		
		this.velX = 0;
		this.velY = 0;
		this.velZ = 0;
		
		this.noClip = false;
		this.hasMass = false;
		this.onGround = false;
		this.stepHeight = 0.0;
		
		this.fallDistance = 0;
		
		this.tempMotionBoundingBox = new AxisAlignedBB();
		
	}
	
	// BASIC UPDATES //
	
	@Override
	public void update() {
		
		super.update();
		this.updateMotion();
		
	}
	
	// MOTION UPDATE //
	
	/**
	 * Internal method to update entity's motion.
	 */
	protected void updateMotion() {
		
		this.setLastPos();
		this.setLastRotation();
		this.updateNaturalVelocity();
		this.move(this.velX, this.velY, this.velZ);
		
		double dx = this.posX - this.lastX;
		double dy = this.posY - this.lastY;
		double dz = this.posZ - this.lastZ;
		
		if (dx != 0 || dy != 0 || dz != 0)
			this.onMoved(dx, dy, dz);
		
	}
	
	@Override
	public void move(double dx, double dy, double dz) {
	
		if (this.noClip) {
			
			super.move(dx, dy, dz);
			this.onGround = false;
			
		} else {
			
			double finalStep = 0;
			double maxStep = 0;
			
			this.tempMotionBoundingBox.setPosition(this.boundingBox);
			this.tempMotionBoundingBox.expand(dx, dy, dz);
			
			List<AxisAlignedBB> bbs = new ArrayList<>();
			this.world.forEachBoundingBoxesIn(this.tempMotionBoundingBox, bbs::add);
			
			if (this.stepHeight != 0) {
				
				for (AxisAlignedBB bb : bbs) {
					
					double step = bb.getMaxY() - this.boundingBox.getMinY();
					
					if (step > finalStep && step <= this.stepHeight) {
						finalStep = step;
					}
						
					double allowedStep = bb.getMinY() - this.boundingBox.getMaxY();
					
					if (allowedStep > maxStep)
						maxStep = allowedStep;
					
				}
				
				if (finalStep <= maxStep) {
					this.boundingBox.move(0, finalStep, 0);
				}
				
			}
			
			if (dy != 0) {
				
				boolean down = dy < 0;
				
				for (AxisAlignedBB bb : bbs) {
					dy = bb.calcOffsetY(this.boundingBox, dy);
				}
				
				this.boundingBox.move(0, dy, 0);
				this.onGround = (down && dy == 0);
			
				if (dy == 0)
					this.velY = 0;
				
			}
			
			if (dx != 0) {
				
				for (AxisAlignedBB bb : bbs) {
					dx = bb.calcOffsetX(this.boundingBox, dx);
				}
				
				this.boundingBox.move(dx, 0, 0);
				
				if (dx == 0)
					this.velX = 0;
				
			}
			
			if (dz != 0) {
			
				for (AxisAlignedBB bb : bbs) {
					dz = bb.calcOffsetZ(this.boundingBox, dz);
				}
				
				this.boundingBox.move(0, 0, dz);
				
				if (dz == 0)
					this.velZ = 0;
			
			}
			
			this.resetPositionToBoundingBox();
			
		}
		
	}
	
	// MOTION EVENTS //
	
	protected void onMoved(double dx, double dy, double dz) {
		
		if (dy >= 0 || this.onGround) {
			
			if (this.onGround)
				this.onFallen(this.fallDistance);
			
			this.fallDistance = 0;
			
		} else {
			this.fallDistance -= dy;
		}
		
	}
	
	public void onFallen(double distance) {
		// Not used //
	}
	
	// POSITIONING //
	
	@Override
	public void setPositionInstant(double x, double y, double z) {
		super.setPositionInstant(x, y, z);
		this.setLastPos();
	}
	
	/**
	 * Set last x, y, z coordinates to current ones.
	 */
	public void setLastPos() {
		
		this.lastX = this.posX;
		this.lastY = this.posY;
		this.lastZ = this.posZ;
		
	}
	
	// BODY ROTATION //
	
	@Override
	public void setRotationInstant(float yaw, float pitch) {
		super.setRotationInstant(yaw, pitch);
		this.setLastRotation();
	}
	
	/**
	 * Set last yaw and last pitch rotations values to current values.
	 */
	public void setLastRotation() {
		
		this.lastYaw = this.yaw;
		this.lastPitch = this.pitch;
		
	}
	
	// VELOCITY //
	
	/**
	 * Define instant velocity of this entity.
	 * @param x X component of velocity.
	 * @param y Y component of velocity.
	 * @param z Z component of velocity.
	 */
	public void setVelocity(double x, double y, double z) {
		
		this.velX = x;
		this.velY = y;
		this.velZ = z;
		
	}
	
	/**
	 * Internal method to update natural velocity, like gravity.
	 */
	protected void updateNaturalVelocity() {
		this.velY -= GRAVITY_FACTOR;
	}
	
	// LINEAR INTERPOLATION METHODS //
	
	public double getLerpedX(float alpha) {
		return MathHelper.interpolate(alpha, this.posX, this.lastX);
	}
	
	public double getLerpedY(float alpha) {
		return MathHelper.interpolate(alpha, this.posY, this.lastY);
	}
	
	public double getLerpedZ(float alpha) {
		return MathHelper.interpolate(alpha, this.posZ, this.lastZ);
	}
	
	public float getLerpedYaw(float alpha) {
		return MathHelper.interpolate(alpha, this.yaw, this.lastYaw);
	}
	
	public float getLerpedPitch(float alpha) {
		return MathHelper.interpolate(alpha, this.pitch, this.lastPitch);
	}
	
	// PROPERTIES GET & SET //
	
	public boolean isNoClip() {
		return this.noClip;
	}
	
	public void setNoClip(boolean noClip) {
		this.noClip = noClip;
	}
	
	public boolean isOnGround() {
		return this.onGround;
	}
	
}
