package fr.theorozier.procgen.renderer.world.block;

import fr.theorozier.procgen.block.Block;
import fr.theorozier.procgen.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class BlockRenderers {

	private static final Map<Short, BlockRenderer> blockRenderers = new HashMap<>();
	
	static {
		
		registerRenderer(Blocks.STONE, new BlockCubeRenderer("stone"));
		registerRenderer(Blocks.DIRT, new BlockCubeRenderer("dirt"));
		registerRenderer(Blocks.GRASS, new BlockGrassRenderer());
		registerRenderer(Blocks.BEDROCK, new BlockCubeRenderer("bedrock"));
		registerRenderer(Blocks.LOG, new BlockLogRenderer());
		registerRenderer(Blocks.LEAVES, new BlockCubeRenderer("leaves"));
		registerRenderer(Blocks.SAND, new BlockCubeRenderer("sand"));
		registerRenderer(Blocks.SANDSTONE, new BlockSandstoneRenderer());
		
		registerRenderer(Blocks.COAL_ORE, new BlockCubeRenderer("coal_ore"));
		registerRenderer(Blocks.IRON_ORE, new BlockCubeRenderer("iron_ore"));
		registerRenderer(Blocks.GOLD_ORE, new BlockCubeRenderer("goal_ore"));
		registerRenderer(Blocks.REDSTONE_ORE, new BlockCubeRenderer("redstone_ore"));
		registerRenderer(Blocks.DIAMOND_ORE, new BlockCubeRenderer("diamond_ore"));
		
	}
	
	public static void registerRenderer(Block block, BlockRenderer renderer) {
		blockRenderers.put(block.getUid(), renderer);
	}
	
	public static BlockRenderer getRenderer(Block block) {
		return blockRenderers.get(block.getUid());
	}

}
