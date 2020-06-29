package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public final class Blocks {
	
	private static final Map<String, Block> identifierRegister = new HashMap<>();
	private static final Map<Short, BlockState> uidStateRegister = new HashMap<>();
	
	public static final BlockAir AIR        = registerBlock(new BlockAir("air"));
	public static final Block STONE         = registerBlock(new Block("stone"));
	public static final Block DIRT          = registerBlock(new Block("dirt"));
	public static final Block GRASS         = registerBlock(new Block("grass"));
	public static final Block BEDROCK       = registerBlock(new Block("bedrock"));
	public static final BlockLog LOG        = registerBlock(new BlockLog("log"));
	public static final BlockLeaves LEAVES  = registerBlock(new BlockLeaves("leaves"));
	public static final Block SAND          = registerBlock(new Block("sand"));
	public static final Block SANDSTONE     = registerBlock(new Block("sandstone"));
	public static final Block GRAVEL        = registerBlock(new Block("gravel"));
	
	public static final Block COAL_ORE      = registerBlock(new Block("coal_ore"));
	public static final Block IRON_ORE      = registerBlock(new Block("iron_ore"));
	public static final Block GOLD_ORE      = registerBlock(new Block("gold_ore"));
	public static final Block REDSTONE_ORE  = registerBlock(new Block("redstone_ore"));
	public static final Block DIAMOND_ORE   = registerBlock(new Block("diamond_ore"));
	
	public static final BlockPlant PLANT_DEADBUSH  = registerBlock(new BlockPlant("plant_deadbush"));
	public static final BlockPlant PLANT_POPPY     = registerBlock(new BlockPlant("plant_poppy"));
	public static final BlockPlant PLANT_DANDELION = registerBlock(new BlockPlant("plant_dandelion"));
	public static final BlockPlant PLANT_OAK       = registerBlock(new BlockPlant("plant_oak"));
	public static final BlockPlant PLANT_GRASS     = registerBlock(new BlockPlant("plant_grass"));
	
	public static final BlockCactus CACTUS  = registerBlock(new BlockCactus("cactus"));
	
	public static final BlockTNT TNT = registerBlock(new BlockTNT("tnt"));
	
	public static final BlockFluid WATER = registerBlock(new BlockFluidWater("water"));
	
	public static <T extends Block> T registerBlock(T block) {
		identifierRegister.put(block.getIdentifier(), block);
		return block;
	}
	
	public static void computeStatesUids() {
	
		short idx = 0;
		
		for (Block block : identifierRegister.values()) {
			for (BlockState state : block.getStateContainer().getStates()) {
				uidStateRegister.put(++idx, state); // Pre-increment to avoid using 0
				state.setUid(idx);
			}
		}
		
	}
	
	public static Block getBlock(String identifier) {
		return identifierRegister.get(identifier);
	}
	
	public static BlockState getBlockState(short uid) {
		return uid == 0 ? null : uidStateRegister.get(uid);
	}
	
	private Blocks() {}
	
}
