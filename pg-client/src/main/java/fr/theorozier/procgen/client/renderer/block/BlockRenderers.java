package fr.theorozier.procgen.client.renderer.block;

import fr.theorozier.procgen.common.block.Block;
import fr.theorozier.procgen.common.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class BlockRenderers {

	private static final Map<String, BlockRenderer> blockRenderers = new HashMap<>();
	
	static {
		
		registerRenderer(Blocks.STONE, new BlockCubeRenderer("stone"));
		registerRenderer(Blocks.DIRT, new BlockCubeRenderer("dirt"));
		registerRenderer(Blocks.GRASS, new BlockGrassRenderer());
		registerRenderer(Blocks.BEDROCK, new BlockCubeRenderer("bedrock"));
		registerRenderer(Blocks.LOG, new BlockLogRenderer());
		registerRenderer(Blocks.LEAVES, new BlockCubeRenderer("leaves_fancy", true));
		registerRenderer(Blocks.SAND, new BlockCubeRenderer("sand"));
		registerRenderer(Blocks.SANDSTONE, new BlockSandstoneRenderer());
		registerRenderer(Blocks.GRAVEL, new BlockCubeRenderer("gravel"));
		
		registerRenderer(Blocks.COAL_ORE, new BlockCubeRenderer("coal_ore"));
		registerRenderer(Blocks.IRON_ORE, new BlockCubeRenderer("iron_ore"));
		registerRenderer(Blocks.GOLD_ORE, new BlockCubeRenderer("goal_ore"));
		registerRenderer(Blocks.REDSTONE_ORE, new BlockCubeRenderer("redstone_ore"));
		registerRenderer(Blocks.DIAMOND_ORE, new BlockCubeRenderer("diamond_ore"));
		
		registerRenderer(Blocks.PLANT_DEADBUSH, new BlockCrossRenderer("plant_deadbush", null));
		registerRenderer(Blocks.PLANT_POPPY, new BlockCrossRenderer("plant_poppy", null));
		registerRenderer(Blocks.PLANT_DANDELION, new BlockCrossRenderer("plant_dandelion", null));
		registerRenderer(Blocks.PLANT_OAK, new BlockCrossRenderer("plant_oak", null));
		registerRenderer(Blocks.PLANT_GRASS, new BlockCrossRenderer("plant_grass", BlockColorResolver.GRASS_COLOR));
		
		registerRenderer(Blocks.CACTUS, new BlockCactusRenderer());
		
		registerRenderer(Blocks.TNT, new BlockTNTRenderer());
		
		registerRenderer(Blocks.WATER, new BlockFluidRenderer("water_still", "water_flow", true, Blocks.WATER));
		
	}
	
	public static void registerRenderer(Block block, BlockRenderer renderer) {
		blockRenderers.put(block.getIdentifier(), renderer);
	}
	
	public static BlockRenderer getRenderer(Block block) {
		return blockRenderers.get(block.getIdentifier());
	}

}
