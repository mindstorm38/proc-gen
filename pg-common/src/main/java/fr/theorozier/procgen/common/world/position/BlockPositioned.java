package fr.theorozier.procgen.common.world.position;

public interface BlockPositioned extends SectionPositioned {
	
	int getY();
	
	ImmutableBlockPosition immutableBlockPos();
	AbsBlockPosition asBlockPos();

	default float distSquared(float x, float y, float z) {
		float dx = this.getX() - x;
		float dy = this.getY() - y;
		float dz = this.getZ() - z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	default float distSquared(BlockPositioned other) {
		return this.distSquared(other.getX(), other.getY(), other.getZ());
	}
	
}
