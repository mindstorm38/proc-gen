package fr.theorozier.procgen.common.world.position;

import io.sutil.pool.FixedObjectPool;

/**
 *
 * A class for mutable section position in the world, with integer coordinates.
 *
 * @author Theo Rozier
 *
 */
public class SectionPosition implements SectionPositioned {

	public static final FixedObjectPool<SectionPosition> POOL = new FixedObjectPool<>(SectionPosition::new, 32);
	
	private int x, z;
	
	public SectionPosition() {}
	
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
	public ImmutableSectionPosition immutableSectionPos() {
		return this.immutable();
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public int getZ() {
		return this.z;
	}
	
	public SectionPosition set(int x, int z) {
		
		this.x = x;
		this.z = z;
		
		return this;
		
	}
	
	public SectionPosition add(int x, int z) {
		
		this.x += x;
		this.z += z;
		
		return this;
		
	}
	
	public SectionPosition add(SectionPositioned sectionPos) {
		return this.add(sectionPos.getX(), sectionPos.getZ());
	}
	
	public SectionPosition sub(int x, int z) {
		
		this.x -= x;
		this.z -= z;
		
		return this;
		
	}
	
	public SectionPosition sub(SectionPositioned sectionPos) {
		return this.sub(sectionPos.getX(), sectionPos.getZ());
	}
	
	@Override
	public boolean equals(Object o) {
		return SectionPositioned.areEquals(this, o);
	}
	
	@Override
	public int hashCode() {
		return SectionPositioned.hashCode(this);
	}
	
	@Override
	public String toString() {
		return SectionPositioned.toString(this);
	}
	
}
