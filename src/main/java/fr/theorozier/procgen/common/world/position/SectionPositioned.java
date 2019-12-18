package fr.theorozier.procgen.common.world.position;

public interface SectionPositioned {

	int getX();
	int getZ();
	
	default float distSquared(float x, float z) {
		float dx = this.getX() - x;
		float dz = this.getZ() - z;
		return dx * dx + dz * dz;
	}

}
