package fr.theorozier.procgen.block;

import fr.theorozier.procgen.world.World;
import fr.theorozier.procgen.world.chunk.WorldBlock;
import fr.theorozier.procgen.world.tick.TickPriority;

import java.util.Random;

public class BlockPlant extends Block {
	
	public BlockPlant(int uid, String identifier) {
		super(uid, identifier);
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_NOT_CULLED;
	}
	
	@Override
	public void initBlock(World world, WorldBlock block) {
		world.getBlockTickList().scheduleTick(this, block.getPosition(), 100, TickPriority.NORMAL);
	}
	
	@Override
	public void tickBlock(World world, WorldBlock block, Random rand) {
	
		/*
		if (rand.nextFloat() < 0.05f) {
			block.setBlockType(Blocks.DIAMOND_ORE);
		} else {
			world.getBlockTickList().scheduleTick(this, block.getPosition(), 20, TickPriority.NORMAL);
		}
		*/
	
	}
	
}
