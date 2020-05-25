package fr.theorozier.procgen.common.world.position;

import io.sutil.pool.FixedObjectPool;
import io.sutil.pool.ObjectPool;

/**
 *
 * A class for mutable block position in world, with integer coordinates.
 *
 * @author Theo Rozier
 *
 */
public class BlockPosition extends AbsBlockPosition {
	
	public static final ObjectPool<BlockPosition> POOL = FixedObjectPool.newSyncFixed(BlockPosition::new, 32);
	
	private int x, y, z;
	
	public BlockPosition() {}
	
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
	
	@Override
	public ImmutableBlockPosition immutableBlockPos() {
		return new ImmutableBlockPosition(this.x, this.y, this.z);
	}
	
	@Override
	public ImmutableSectionPosition immutableSectionPos() {
		return new ImmutableSectionPosition(this.x, this.z);
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
	
	public BlockPosition set(int x, int y, int z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
		
	}
	
	public BlockPosition set(BlockPositioned pos, int dx, int dy, int dz) {
		return this.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
	}
	
	public BlockPosition set(BlockPositioned pos, Direction dir) {
		return this.set(pos.getX() + dir.rx, pos.getY() + dir.ry, pos.getZ() + dir.rz);
	}
	
	public BlockPosition set(BlockPositioned pos) {
		return this.set(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public BlockPosition add(int x, int y, int z) {
		
		this.x += x;
		this.y += y;
		this.z += z;
		
		return this;
		
	}
	
	public BlockPosition add(BlockPositioned blockPos) {
		return this.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public BlockPosition add(SectionPositioned sectionPos) {
		return this.add(sectionPos.getX(), 0, sectionPos.getZ());
	}
	
	public BlockPosition add(Direction dir) {
		return this.add(dir.rx, dir.ry, dir.rz);
	}
	
	public BlockPosition add(Direction dir, int xf, int yf, int zf) {
		return this.add(dir.rx * xf, dir.ry * yf, dir.rz * zf);
	}
	
	public BlockPosition sub(int x, int y, int z) {
		
		this.x -= x;
		this.y -= y;
		this.z -= z;
		
		return this;
		
	}
	
	public BlockPosition sub(BlockPositioned blockPos) {
		return this.sub(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public BlockPosition sub(SectionPositioned sectionPos) {
		return this.sub(sectionPos.getX(), 0, sectionPos.getZ());
	}
	
}
