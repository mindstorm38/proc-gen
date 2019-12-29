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
	
}
