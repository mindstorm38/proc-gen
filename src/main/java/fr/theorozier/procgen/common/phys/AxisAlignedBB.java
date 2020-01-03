package fr.theorozier.procgen.common.phys;

public class AxisAlignedBB {

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
		this.minX = minZ;
		
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
	
	public boolean intersect(double x, double y, double z) {
		return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
	}
	
	public boolean intersect(AxisAlignedBB bb) {
		return this.minX < bb.maxX && this.maxX > bb.minX &&
				this.minY < bb.maxY && this.maxY > bb.minY &&
				this.minZ < bb.maxZ && this.maxZ > bb.minZ;
	}
	
	public double calcOffsetX(AxisAlignedBB other, double offsetX) {
		
		if (this.minY < other.maxY && this.maxY > other.minY && this.minZ < other.maxZ && this.maxZ > other.minZ) {
			
			if (offsetX > 0 && other.maxX > this.maxX) {
				
				double d = other.minX - this.maxX;
				
				if (d < offsetX) {
					offsetX = d;
				}
				
			} else if (offsetX < 0 && other.minX < this.minX) {
				
				double d = other.minX - this.maxX;
				
				if (d > offsetX) {
					offsetX = d;
				}
				
			}
			
		}
		
		return offsetX;
		
	}
	
	public double calcOffsetY(AxisAlignedBB other, double offsetY) {
	
		if (this.minX < other.maxX && this.maxX > other.minX && this.minZ < other.maxZ && this.maxZ > other.minZ) {
			
			if (offsetY > 0 && other.maxY > this.maxY) {
				
				double d = other.minY - this.maxY;
				
				if (d < offsetY) {
					offsetY = d;
				}
				
			} else if (offsetY < 0 && other.minY < this.minY) {
				
				double d = other.minY - this.maxY;
				
				if (d > offsetY) {
					offsetY = d;
				}
				
			}
			
		}
		
		return offsetY;
	
	}
	
	public double calcOffsetZ(AxisAlignedBB other, double offsetZ) {
		
		if (this.minY < other.maxY && this.maxY > other.minY && this.minX < other.maxX && this.maxX > other.minX) {
			
			if (offsetZ > 0 && other.maxZ > this.maxZ) {
				
				double d = other.minZ - this.maxZ;
				
				if (d < offsetZ) {
					offsetZ = d;
				}
				
			} else if (offsetZ < 0 && other.minZ < this.minZ) {
				
				double d = other.minZ - this.maxZ;
				
				if (d > offsetZ) {
					offsetZ = d;
				}
				
			}
			
		}
		
		return offsetZ;
		
	}
	
}
