package fr.theorozier.procgen.common.phys;

public class AxisAlignedBB {

	private float minX, minY, minZ;
	private float maxX, maxY, maxZ;
	
	public AxisAlignedBB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.setPosition(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public AxisAlignedBB() {
		this.setPosition(0, 0, 0, 0, 0, 0);
	}
	
	public float getMinX() {
		return minX;
	}
	
	public float getMinY() {
		return minY;
	}
	
	public float getMinZ() {
		return minZ;
	}
	
	public float getMaxX() {
		return maxX;
	}
	
	public float getMaxY() {
		return maxY;
	}
	
	public float getMaxZ() {
		return maxZ;
	}
	
	public float getSizeX() {
		return this.maxX - this.minX;
	}
	
	public float getSizeY() {
		return this.maxY - this.minY;
	}
	
	public float getSizeZ() {
		return this.maxZ - this.minZ;
	}
	
	public float getMiddleX() {
		return (this.maxX + this.minX) / 2f;
	}
	
	public float getMiddleY() {
		return (this.maxY + this.minY) / 2f;
	}
	
	public float getMiddleZ() {
		return (this.maxZ + this.minZ) / 2f;
	}
	
	public void setPosition(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.minZ = Math.min(minZ, maxZ);
		
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
		this.maxZ = Math.max(minZ, maxZ);
		
	}
	
	public void setPositionUnsafe(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		
		this.minX = minX;
		this.minY = minY;
		this.minX = minZ;
		
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		
	}
	
	public void move(float dx, float dy, float dz) {
		
		this.minX += dx;
		this.maxX += dx;
		this.minY += dy;
		this.maxY += dy;
		this.minZ += dz;
		this.maxZ += dz;
		
	}
	
	public boolean intersect(float x, float y, float z) {
		return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
	}
	
	public boolean intersect(AxisAlignedBB bb) {
		return this.minX < bb.maxX && this.maxX > bb.minX &&
				this.minY < bb.maxY && this.maxY > bb.minY &&
				this.minZ < bb.maxZ && this.maxZ > bb.minZ;
	}
	
	public float calcOffsetX(AxisAlignedBB other, float offsetX) {
		
		if (this.minY < other.maxY && this.maxY > other.minY && this.minZ < other.maxZ && this.maxZ > other.minZ) {
			
			if (offsetX > 0 && other.maxX > this.maxX) {
				
				float d = other.minX - this.maxX;
				
				if (d < offsetX) {
					offsetX = d;
				}
				
			} else if (offsetX < 0 && other.minX < this.minX) {
				
				float d = other.minX - this.maxX;
				
				if (d > offsetX) {
					offsetX = d;
				}
				
			}
			
		}
		
		return offsetX;
		
	}
	
	public float calcOffsetY(AxisAlignedBB other, float offsetY) {
	
		if (this.minX < other.maxX && this.maxX > other.minX && this.minZ < other.maxZ && this.maxZ > other.minZ) {
			
			if (offsetY > 0 && other.maxY > this.maxY) {
				
				float d = other.minY - this.maxY;
				
				if (d < offsetY) {
					offsetY = d;
				}
				
			} else if (offsetY < 0 && other.minY < this.minY) {
				
				float d = other.minY - this.maxY;
				
				if (d > offsetY) {
					offsetY = d;
				}
				
			}
			
		}
		
		return offsetY;
	
	}
	
	public float calcOffsetZ(AxisAlignedBB other, float offsetZ) {
		
		if (this.minY < other.maxY && this.maxY > other.minY && this.minX < other.maxX && this.maxX > other.minX) {
			
			if (offsetZ > 0 && other.maxZ > this.maxZ) {
				
				float d = other.minZ - this.maxZ;
				
				if (d < offsetZ) {
					offsetZ = d;
				}
				
			} else if (offsetZ < 0 && other.minZ < this.minZ) {
				
				float d = other.minZ - this.maxZ;
				
				if (d > offsetZ) {
					offsetZ = d;
				}
				
			}
			
		}
		
		return offsetZ;
		
	}
	
}
