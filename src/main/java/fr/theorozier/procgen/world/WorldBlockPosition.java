package fr.theorozier.procgen.world;

public class WorldBlockPosition {
	
	private final int x, y, z;
	
	public WorldBlockPosition(int x, int y, int z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public WorldBlockPosition sub(int x, int y, int z) {
		return new WorldBlockPosition(this.x - x, this.y - y, this.z - z);
	}
	
	public WorldBlockPosition sub(WorldBlockPosition other) {
		return new WorldBlockPosition(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public WorldBlockPosition add(int x, int y, int z) {
		return new WorldBlockPosition(this.x + x, this.y + y, this.z + z);
	}
	
	public float distSquared(float x, float y, float z) {
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	public float dist(float x, float y, float z) {
		float dx = this.x - x;
		float dy = this.y - y;
		float dz = this.z - z;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WorldBlockPosition t = (WorldBlockPosition) o;
		return x == t.x && y == t.y && z == t.z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}
	
	@Override
	public String toString() {
		return "<" + x + "," + y + "," + z + ">";
	}
}
