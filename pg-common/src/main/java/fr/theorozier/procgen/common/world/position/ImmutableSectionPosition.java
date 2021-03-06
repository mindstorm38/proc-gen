package fr.theorozier.procgen.common.world.position;

public class ImmutableSectionPosition extends AbsSectionPosition {
	
	private final int x, z;
	
	public ImmutableSectionPosition(int x, int z) {
		
		this.x = x;
		this.z = z;
		
	}
	
	public ImmutableSectionPosition(SectionPositioned sectionPos) {
		this(sectionPos.getX(), sectionPos.getZ());
	}
	
	public SectionPosition mutate() {
		return new SectionPosition(this.x, this.z);
	}
	
	@Override
	public ImmutableSectionPosition immutableSectionPos() {
		return this;
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public int getZ() {
		return this.z;
	}
	
}
