package fr.theorozier.procgen.common.world.position;

import java.util.Objects;

/**
 *
 * A class for mutable block position in world, with integer coordinates.
 *
 * @author Theo Rozier
 *
 */
public class BlockPosition implements BlockPositioned {
	
	private int x, y, z;
	
	public BlockPosition(int x, int y, int z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public BlockPosition(BlockPositioned blockPos) {
		this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public BlockPosition(SectionPositioned sectionPos, int y) {
		this(sectionPos.getX(), y, sectionPos.getZ());
	}
	
	public BlockPosition copy() {
		return new BlockPosition(this.x, this.y, this.z);
	}
	
	public ImmutableBlockPosition immutable() {
		return new ImmutableBlockPosition(this.x, this.y, this.z);
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public int getY() {
		return this.y;
	}
	
	@Override
	public int getZ() {
		return this.z;
	}
	
	public void add(int x, int y, int z) {
		
		this.x += x;
		this.y += y;
		this.z += z;
		
	}
	
	public void add(BlockPositioned blockPos) {
		this.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public void add(SectionPositioned sectionPos) {
		this.add(sectionPos.getX(), 0, sectionPos.getZ());
	}
	
	public void sub(int x, int y, int z) {
		
		this.x -= x;
		this.y -= y;
		this.z -= z;
		
	}
	
	public void sub(BlockPositioned blockPos) {
		this.sub(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public void sub(SectionPositioned sectionPos) {
		this.sub(sectionPos.getX(), 0, sectionPos.getZ());
	}
	
	@Override
	public boolean equals(Object o) {
		return BlockPositioned.areEquals(this, o);
	}
	
	@Override
	public int hashCode() {
		return BlockPositioned.hashCode(this);
	}
	
}
