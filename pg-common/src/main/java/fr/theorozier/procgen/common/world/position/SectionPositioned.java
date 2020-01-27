package fr.theorozier.procgen.common.world.position;

public interface SectionPositioned {

	int getX();
	int getZ();
	
	ImmutableSectionPosition immutableSectionPos();
	
	default float distSquared(float x, float z) {
		float dx = this.getX() - x;
		float dz = this.getZ() - z;
		return dx * dx + dz * dz;
	}
	
	default float distSquared(SectionPositioned other) {
		return this.distSquared(other.getX(), other.getZ());
	}
	
	static int hashCode(SectionPositioned sectionPos) {
		int result = 31 + sectionPos.getX();
		result = 31 * result + sectionPos.getZ();
		return result;
	}
	
	static boolean areEquals(SectionPositioned pos, Object o) {
		if (pos == o) return true;
		if (!(o instanceof SectionPositioned)) return false;
		SectionPositioned that = (SectionPositioned) o;
		return pos.getX() == that.getX() && pos.getZ() == that.getZ();
	}
	
	static String toString(SectionPositioned pos) {
		return "<" + pos.getX() + "/" + pos.getZ() + ">";
	}
	
}
