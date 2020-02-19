package fr.theorozier.procgen.common.world.position;

public interface SectionPositioned {

	// TODO Need to rework this package, in order to extends abstract class instead
	//      of implementing indenpendently these interfaces.
	//      Currently it's an issue because we (and it will no longer have to be done)
	//      use interface has keys for maps, and for example if we have a map mapped by
	//      SectionPositioned keys, we are still able to put and call contains with
	//      BlockPositioned and it's an issue because classes extending BlockPositioned
	//      don't have this same hashcode() implementation.

	int getX();
	int getZ();
	
	ImmutableSectionPosition immutableSectionPos();
	AbsSectionPosition asSectionPos();

	default float distSquared(float x, float z) {
		float dx = this.getX() - x;
		float dz = this.getZ() - z;
		return dx * dx + dz * dz;
	}
	
	default float distSquared(SectionPositioned other) {
		return this.distSquared(other.getX(), other.getZ());
	}
	
}
