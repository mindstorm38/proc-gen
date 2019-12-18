package fr.theorozier.procgen.common.world.position;

public class ImmutableBlockPosition implements BlockPositioned {
	
	private final int x, y, z;
	
	public ImmutableBlockPosition(int x, int y, int z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public ImmutableBlockPosition(BlockPositioned blockPos) {
		this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public ImmutableBlockPosition(SectionPositioned sectionPos, int y) {
		this(sectionPos.getX(), y, sectionPos.getZ());
	}
	
	public BlockPosition mutate() {
		return new BlockPosition(this.x, this.y, this.z);
	}
	
	@Override
	public int getX() {
		return 0;
	}
	
	@Override
	public int getY() {
		return 0;
	}
	
	@Override
	public int getZ() {
		return 0;
	}
	
}
