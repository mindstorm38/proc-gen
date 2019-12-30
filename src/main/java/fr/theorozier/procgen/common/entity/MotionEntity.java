package fr.theorozier.procgen.common.entity;

import fr.theorozier.procgen.common.phys.AxisAlignedBB;
import fr.theorozier.procgen.common.world.WorldBase;
import fr.theorozier.procgen.common.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

public class MotionEntity extends Entity {
	
	protected boolean noClip;
	protected boolean hasMass;
	protected boolean onGround;
	protected float stepHeight;
	
	protected float velX;
	protected float velY;
	protected float velZ;
	
	protected float lastX;
	protected float lastY;
	protected float lastZ;
	
	public MotionEntity(WorldBase world, WorldServer serverWorld, long uid) {
		
		super(world, serverWorld, uid);
		
		this.noClip = false;
		this.hasMass = false;
		this.onGround = false;
		this.stepHeight = 0f;
		
	}
	
	@Override
	public void update() {
		
		super.update();
		
		this.setLastPos();
		this.updateNaturalVelocity();
		
		this.move(this.velX, this.velY, this.velZ);
		
	}
	
	@Override
	public void move(float dx, float dy, float dz) {
	
		if (this.noClip) {
			
			super.move(dx, dy, dz);
			this.onGround = false;
			
		} else {
			
			float finalStep = 0;
			float maxStep = 0;
			
			List<AxisAlignedBB> bbs = new ArrayList<>();
			this.world.forEachBoundingBoxesIn(this.boundingBox, bbs::add);
			
			if (this.stepHeight != 0) {
				
				for (AxisAlignedBB bb : bbs) {
					
					if (bb.intersect(this.boundingBox)) {
						
						float step = bb.getMaxY() - this.boundingBox.getMinY();
						
						if (step > finalStep && step <= this.stepHeight) {
							finalStep = step;
						}
						
					}
					
					float allowedStep = bb.getMinY() - this.boundingBox.getMaxY();
					
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
					dy = this.boundingBox.calcOffsetY(bb, dy);
				}
				
				this.boundingBox.move(0, dy, 0);
				this.onGround = (down && dy == 0);
			
			}
			
			if (dx != 0) {
				
				for (AxisAlignedBB bb : bbs) {
					dx = this.boundingBox.calcOffsetX(bb, dx);
				}
				
				this.boundingBox.move(dx, 0, 0);
				
			}
			
			if (dz != 0) {
			
				for (AxisAlignedBB bb : bbs) {
					dz = this.boundingBox.calcOffsetZ(bb, dz);
				}
				
				this.boundingBox.move(0, 0, dz);
			
			}
			
			this.resetPositionToBoundingBox();
			
		}
		
	}
	
	protected void updateNaturalVelocity() {
		this.velY -= GRAVITY_FACTOR;
	}
	
	public void setLastPos() {
		
		this.lastX = this.posX;
		this.lastY = this.posY;
		this.lastZ = this.posZ;
		
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
	
	public void setVelocity(float x, float y, float z) {
		
		this.velX = x;
		this.velY = y;
		this.velZ = z;
		
	}
	
}
