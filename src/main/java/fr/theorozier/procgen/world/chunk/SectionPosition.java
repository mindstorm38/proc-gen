package fr.theorozier.procgen.world.chunk;

import fr.theorozier.procgen.world.BlockPosition;
import fr.theorozier.procgen.world.HorizontalPosition;

public class SectionPosition implements HorizontalPosition {

	private final int x, z;
	
	public SectionPosition(int x, int z) {
		
		this.x = x;
		this.z = z;
		
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public int getZ() {
		return this.z;
	}
	
	public SectionPosition sub(SectionPosition other) {
		return new SectionPosition(this.x - other.x, this.z - other.z);
	}
	
	public SectionPosition add(SectionPosition other) {
		return new SectionPosition(this.x + other.x, this.z + other.z);
	}
	
	public BlockPosition getChunkPos(int y) {
		return new BlockPosition(this.x, y, this.z);
	}
	
	public boolean isInSection(int x, int z) {
		return x >= this.x && x < this.x + 16 && z >= this.z && z < this.z + 16;
	}
	
	public boolean isInSection(SectionPosition other) {
		return this.isInSection(other.x, other.z);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SectionPosition t = (SectionPosition) o;
		return x == t.x && z == t.z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}
	
	@Override
	public String toString() {
		return "<" + x + "," + z + ">";
	}

}
