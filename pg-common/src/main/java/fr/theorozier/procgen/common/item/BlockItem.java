package fr.theorozier.procgen.common.item;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.LiveEntity;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.position.BlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;
import fr.theorozier.procgen.common.world.position.Direction;
import io.sutil.pool.FixedObjectPool;

public class BlockItem extends Item {
	
	private final BlockState block;
	
	public BlockItem(int uid, String identifier, BlockState block) {
		super(uid, identifier);
		this.block = block;
	}
	
	public BlockItem(int uid, String identifier, Block block) {
		this(uid, identifier, block.getDefaultState());
	}
	
	@Override
	public void useOnBlockNoInteract(WorldDimension world, LiveEntity entity, ItemStack stack, BlockPositioned pos, BlockState block, Direction face) {
		
		try (FixedObjectPool<BlockPosition>.PoolObject poolPos = BlockPosition.POOL.acquire()) {
			
			BlockPosition placePos = poolPos.get().set(pos).add(face);
			
			BlockState stateAt = world.getBlockAt(placePos);
			
			if (stateAt == null || stateAt.getBlock().canOverride(world, placePos, stateAt)) {
				
				BlockState stateToPlace = this.getStateToPlace(world, entity, stack, pos, block, face);
				
				if (stateToPlace != null) {
					
					int currentCount = stack.getCount();
					if (currentCount != 0) {
						stack.setCount((byte) (currentCount - 1));
					}
					
					world.setBlockAt(placePos, stateToPlace);
					
				}
				
			}
			
		}
		
	}
	
	public BlockState getStateToPlace(WorldDimension world, LiveEntity entity, ItemStack stack, BlockPositioned pos, BlockState block, Direction face) {
		return this.block;
	}
	
}
