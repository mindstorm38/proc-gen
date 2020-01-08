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
	
	protected double fallDistance;
	
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
		
	}
	
	@Override
	public void update() {
		
		super.update();
		this.updateMotion();
		
	}
	
	/**
	 * Internal method to update entity's motion.
	 */
	protected void updateMotion() {
		
		this.setLastPos();
		this.updateNaturalVelocity();
		this.move(this.velX, this.velY, this.velZ);
		
		double dy = this.posY - this.lastY;
		
		if (dy >= 0 || this.onGround) {
			
			if (this.onGround)
				this.fallen(this.fallDistance);
			
			this.fallDistance = 0;
			
		} else {
			this.fallDistance -= dy;
		}
		
	}
	
	@Override
	public void setPositionInstant(double x, double y, double z) {
		super.setPositionInstant(x, y, z);
		this.setLastPos();
	}
	
	@Override
	public void move(double dx, double dy, double dz) {
	
		if (this.noClip) {
			
			super.move(dx, dy, dz);
			this.onGround = false;
			
		} else {
			
			double finalStep = 0;
			double maxStep = 0;
			
			AxisAlignedBB newBoundingBox = new AxisAlignedBB(this.boundingBox);
			newBoundingBox.expand(dx, dy, dz);
			
			List<AxisAlignedBB> bbs = new ArrayList<>();
			this.world.forEachBoundingBoxesIn(newBoundingBox, bbs::add);
			
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
	
	public void fallen(double distance) { }
	
	/**
	 * Internal method to update natural velocity, like gravity.
	 */
	protected void updateNaturalVelocity() {
		this.velY -= GRAVITY_FACTOR;
	}
	
	protected void setLastPos() {
		
		this.lastX = this.posX;
		this.lastY = this.posY;
		this.lastZ = this.posZ;
		
	}
	
	public double getLerpedX(float alpha) {
		return MathHelper.interpolate(alpha, this.posX, this.lastX);
	}
	
	public double getLerpedY(float alpha) {
		return MathHelper.interpolate(alpha, this.posY, this.lastY);
	}
	
	public double getLerpedZ(float alpha) {
		return MathHelper.interpolate(alpha, this.posZ, this.lastZ);
	}
	
	public boolean isNoClip() {
		return this.noClip;
	}
	
	public void setNoClip(boolean noClip) {
		this.noClip = noClip;
	}
	
	public boolean isOnGround() {
		return this.onGround;
	}
	
	public void setVelocity(double x, double y, double z) {
		
		this.velX = x;
		this.velY = y;
		this.velZ = z;
		
	}
	
}
