package fr.theorozier.procgen.common.world.position;

public interface BlockPositioned extends SectionPositioned {
	
	int getY();
	
	default float distSquared(float x, float y, float z) {
		float dx = this.getX() - x;
		float dy = this.getY() - y;
		float dz = this.getZ() - z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	static int hashCode(BlockPositioned blockPos) {
		int result = 31 + blockPos.getX();
		result = 31 * result + blockPos.getY();
		result = 31 * result + blockPos.getZ();
		return result;
	}
	
	static boolean areEquals(BlockPositioned pos, Object o) {
		if (pos == o) return true;
		if (!(o instanceof BlockPositioned)) return false;
		BlockPositioned that = (BlockPositioned) o;
		return pos.getX() == that.getX() && pos.getY() == that.getY() && pos.getZ() == that.getZ();
	}
	
}
