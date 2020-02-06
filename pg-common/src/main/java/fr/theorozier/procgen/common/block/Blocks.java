package fr.theorozier.procgen.common.block;

import fr.theorozier.procgen.common.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public final class Blocks {
	
	private static final Map<Short, Block> uidRegister = new HashMap<>();
	private static final Map<String, Block> identifierRegister = new HashMap<>();
	private static final Map<Short, BlockState> uidStateRegister = new HashMap<>();
	
	public static final BlockAir AIR        = registerBlock(new BlockAir(1, "air"));
	public static final Block STONE         = registerBlock(new Block(2, "stone"));
	public static final Block DIRT          = registerBlock(new Block(3, "dirt"));
	public static final Block GRASS         = registerBlock(new Block(4, "grass"));
	public static final Block BEDROCK       = registerBlock(new Block(5, "bedrock"));
	public static final BlockLog LOG        = registerBlock(new BlockLog(6, "log"));
	public static final BlockLeaves LEAVES  = registerBlock(new BlockLeaves(7, "leaves"));
	public static final Block SAND          = registerBlock(new Block(8, "sand"));
	public static final Block SANDSTONE     = registerBlock(new Block(9, "sandstone"));
	public static final Block GRAVEL        = registerBlock(new Block(10, "gravel"));
	
	public static final Block COAL_ORE      = registerBlock(new Block(20, "coal_ore"));
	public static final Block IRON_ORE      = registerBlock(new Block(21, "iron_ore"));
	public static final Block GOLD_ORE      = registerBlock(new Block(22, "gold_ore"));
	public static final Block REDSTONE_ORE  = registerBlock(new Block(23, "redstone_ore"));
	public static final Block DIAMOND_ORE   = registerBlock(new Block(24, "diamond_ore"));
	
	public static final BlockPlant PLANT_DEADBUSH  = registerBlock(new BlockPlant(30, "plant_deadbush"));
	public static final BlockPlant PLANT_POPPY     = registerBlock(new BlockPlant(31, "plant_poppy"));
	public static final BlockPlant PLANT_DANDELION = registerBlock(new BlockPlant(32, "plant_dandelion"));
	public static final BlockPlant PLANT_OAK       = registerBlock(new BlockPlant(33, "plant_oak"));
	public static final BlockPlant PLANT_GRASS     = registerBlock(new BlockPlant(34, "plant_grass"));
	
	public static final BlockCactus CACTUS  = registerBlock(new BlockCactus(40, "cactus"));
	
	public static final BlockTNT TNT = registerBlock(new BlockTNT(50, "tnt"));
	
	public static final BlockFluid WATER = registerBlock(new BlockFluidWater(80, "water"));
	
	public static <T extends Block> T registerBlock(T block) {
		
		uidRegister.put(block.getUid(), block);
		identifierRegister.put(block.getIdentifier(), block);
		
		return block;
		
	}
	
	public static void computeStatesUids() {
	
		short idx = 0;
		
		for (Block block : uidRegister.values()) {
			for (BlockState state : block.getStateContainer().getStates()) {
				uidStateRegister.put(++idx, state);
				state.setUid(idx);
			}
		}
		
	}
	
	public static Block getBlock(short uid) {
		return uidRegister.get(uid);
	}
	
	public static Block getBlock(String identifier) {
		return identifierRegister.get(identifier);
	}
	
	public static BlockState getBlockState(short uid) {
		return uidStateRegister.get(uid);
	}
	
	private Blocks() {}
	
}
