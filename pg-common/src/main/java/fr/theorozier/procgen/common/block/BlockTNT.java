package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;
import fr.theorozier.procgen.common.entity.Entity;
import fr.theorozier.procgen.common.entity.PrimedTNTEntity;
import fr.theorozier.procgen.common.world.WorldDimension;
import fr.theorozier.procgen.common.world.position.AbsBlockPosition;
import fr.theorozier.procgen.common.world.position.BlockPositioned;

public class BlockTNT extends Block {
	
	public BlockTNT(String identifier) {
		super(identifier);
	}
	
	public PrimedTNTEntity fuze(WorldDimension world, AbsBlockPosition pos, BlockState state) {
		
		PrimedTNTEntity entity = new PrimedTNTEntity(world, Entity.getNewUid());
		entity.setPositionInstant(pos.getX(), pos.getY(), pos.getZ());
		
		world.setBlockAt(pos, Blocks.AIR.getDefaultState());
		world.spawnEntity(entity);
		
		return entity;
		
	}
	
}
