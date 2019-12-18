package fr.theorozier.procgen.common.world.position;

public interface BlockPositioned extends SectionPositioned {
	
	int getY();
	
	default float distSquared(float x, float y, float z) {
		float dx = this.getX() - x;
		float dy = this.getY() - y;
		float dz = this.getZ() - z;
		return dx * dx + dy * dy + dz * dz;
	}
	
}
