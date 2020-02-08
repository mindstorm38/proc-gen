package fr.theorozier.procgen.common.phys;

import io.sutil.pool.FixedObjectPool;

public class AxisAlignedBB {

	public static final FixedObjectPool<AxisAlignedBB> POOL = new FixedObjectPool<>(AxisAlignedBB::new, 16);
	
	private double minX, minY, minZ;
	private double maxX, maxY, maxZ;
	
	public AxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.setPosition(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public AxisAlignedBB() {
		this.setPosition(0, 0, 0, 0, 0, 0);
	}
	
	public AxisAlignedBB(AxisAlignedBB bb) {
		this.setPosition(bb);
	}
	
	public double getMinX() {
		return minX;
	}
	
	public double getMinY() {
		return minY;
	}
	
	public double getMinZ() {
		return minZ;
	}
	
	public double getMaxX() {
		return maxX;
	}
	
	public double getMaxY() {
		return maxY;
	}
	
	public double getMaxZ() {
		return maxZ;
	}
	
	public double getSizeX() {
		return this.maxX - this.minX;
	}
	
	public double getSizeY() {
		return this.maxY - this.minY;
	}
	
	public double getSizeZ() {
		return this.maxZ - this.minZ;
	}
	
	public double getMiddleX() {
		return (this.maxX + this.minX) / 2.0;
	}
	
	public double getMiddleY() {
		return (this.maxY + this.minY) / 2.0;
	}
	
	public double getMiddleZ() {
		return (this.maxZ + this.minZ) / 2.0;
	}
	
	public void setPosition(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.minZ = Math.min(minZ, maxZ);
		
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
		this.maxZ = Math.max(minZ, maxZ);
		
	}
	
	public void setPosition(AxisAlignedBB bb) {
		this.setPositionUnsafe(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}
	
	public void setPositionUnsafe(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		
	}
	
	public void move(double dx, double dy, double dz) {
		
		this.minX += dx;
		this.maxX += dx;
		this.minY += dy;
		this.maxY += dy;
		this.minZ += dz;
		this.maxZ += dz;
		
	}
	
	public void expand(double x, double y, double z) {
		
		if (x > 0) this.maxX += x;
		else this.minX += x;
		
		if (y > 0) this.maxY += y;
		else this.minY += y;
		
		if (z > 0) this.maxZ += z;
		else this.minZ += z;
		
	}
	
	public void grow(double x, double y, double z) {
		
		this.minX -= x;
		this.maxX += x;
		
		this.minY -= y;
		this.maxY += y;
		
		this.minZ -= z;
		this.maxZ += z;
		
	}
	
	public boolean intersects(double x, double y, double z) {
		return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
	}
	
	public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
		return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1 && this.minZ < z2 && this.maxZ > z1;
	}
	
	public boolean intersects(AxisAlignedBB other) {
		return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
	}
	
	public double calcOffsetX(AxisAlignedBB other, double offsetX) {
		
		if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
			
			if (offsetX > 0.0 && other.maxX <= this.minX) {
				
				double d = this.minX - other.maxX;
				
				if (d < offsetX) {
					offsetX = d;
				}
				
			} else if (offsetX < 0.0 && other.minX >= this.maxX) {
				
				double d = this.maxX - other.minX;
				
				if (d > offsetX) {
					offsetX = d;
				}
				
			}
			
		}
		
		return offsetX;
		
	}
	
	public double calcOffsetY(AxisAlignedBB other, double offsetY) {
		
		if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
			
			if (offsetY > 0.0 && other.maxY <= this.minY) {
				
				double d = this.minY - other.maxY;
				
				if (d < offsetY) {
					offsetY = d;
				}
				
			} else if (offsetY < 0.0 && other.minY >= this.maxY) {
				
				double d = this.maxY - other.minY;
				
				if (d > offsetY) {
					offsetY = d;
				}
				
			}
			
		}
		
		return offsetY;
		
	}
	
	public double calcOffsetZ(AxisAlignedBB other, double offsetZ) {
		
		if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
			
			if (offsetZ > 0.0 && other.maxZ <= this.minZ) {
				
				double d = this.minZ - other.maxZ;
				
				if (d < offsetZ) {
					offsetZ = d;
				}
				
			} else if (offsetZ < 0.0 && other.minZ >= this.maxZ) {
				
				double d = this.maxZ - other.minZ;
				
				if (d > offsetZ) {
					offsetZ = d;
				}
				
			}
			
		}
		
		return offsetZ;
		
	}
	
	@Override
	public String toString() {
		return "AABB{" +
				"minX=" + minX +
				", minY=" + minY +
				", minZ=" + minZ +
				", maxX=" + maxX +
				", maxY=" + maxY +
				", maxZ=" + maxZ +
				'}';
	}
	
}
