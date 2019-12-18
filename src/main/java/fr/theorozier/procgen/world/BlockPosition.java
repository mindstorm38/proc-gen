package fr.theorozier.procgen.world;

import fr.theorozier.procgen.world.chunk.SectionPosition;
import fr.theorozier.procgen.world.util.Direction;

public class BlockPosition implements HorizontalPosition {
	
	private final int x, y, z;
	
	public BlockPosition(int x, int y, int z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public BlockPosition(HorizontalPosition pos, int y) {
		this(pos.getX(), y, pos.getZ());
	}
	
	@Override
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public int getZ() {
		return z;
	}
	
	public BlockPosition sub(int x, int y, int z) {
		return new BlockPosition(this.x - x, this.y - y, this.z - z);
	}
	
	public BlockPosition sub(BlockPosition other) {
		return new BlockPosition(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public BlockPosition add(int x, int y, int z) {
		return new BlockPosition(this.x + x, this.y + y, this.z + z);
	}
	
	public BlockPosition add(Direction dir, int factX, int factY, int factZ) {
		return this.add(dir.rx * factX, dir.ry * factY, dir.rz * factZ);
	}
	
	public BlockPosition add(Direction dir) {
		return this.add(dir, 1, 1, 1);
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
	
	public SectionPosition toSectionPosition() {
		return new SectionPosition(this.x, this.z);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockPosition t = (BlockPosition) o;
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
