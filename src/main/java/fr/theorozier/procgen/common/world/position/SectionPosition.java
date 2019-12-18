package fr.theorozier.procgen.common.world.position;

/**
 *
 * A class for mutable section position in the world, with integer coordinates.
 *
 * @author Theo Rozier
 *
 */
public class SectionPosition implements SectionPositioned {

	private int x, z;
	
	public SectionPosition(int x, int z) {
		
		this.x = x;
		this.z = z;
		
	}
	
	public SectionPosition(SectionPositioned sectionPos) {
		this(sectionPos.getX(), sectionPos.getZ());
	}
	
	public SectionPosition copy() {
		return new SectionPosition(this.x, this.z);
	}
	
	public ImmutableSectionPosition immutable() {
		return new ImmutableSectionPosition(this.x, this.z);
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public int getZ() {
		return this.z;
	}
	
	public void add(int x, int z) {
		
		this.x += x;
		this.z += z;
		
	}
	
	public void add(SectionPositioned sectionPos) {
		this.add(sectionPos.getX(), sectionPos.getZ());
	}
	
	public void sub(int x, int z) {
		
		this.x -= x;
		this.z -= z;
		
	}
	
	public void sub(SectionPositioned sectionPos) {
		this.sub(sectionPos.getX(), sectionPos.getZ());
	}
	
}
