package fr.theorozier.procgen.block;

import java.util.HashMap;
import java.util.Map;

public class Blocks {
	
	private static final Map<Short, Block> uidRegister = new HashMap<>();
	private static final Map<String, Block> identifierRegister = new HashMap<>();
	
	public static final BlockAir AIR        = registerBlock(new BlockAir(1, "air"));
	public static final Block STONE         = registerBlock(new Block(2, "stone"));
	public static final Block DIRT          = registerBlock(new Block(3, "dirt"));
	public static final Block GRASS         = registerBlock(new Block(4, "grass"));
	public static final Block BEDROCK       = registerBlock(new Block(5, "bedrock"));
	public static final BlockLog LOG        = registerBlock(new BlockLog(6, "log"));
	public static final BlockLeaves LEAVES  = registerBlock(new BlockLeaves(7, "leaves"));
	public static final Block SAND          = registerBlock(new Block(8, "sand"));
	public static final Block SANDSTONE     = registerBlock(new Block(9, "sandstone"));
	
	public static final Block COAL_ORE      = registerBlock(new Block(20, "coal_ore"));
	public static final Block IRON_ORE      = registerBlock(new Block(21, "iron_ore"));
	public static final Block GOLD_ORE      = registerBlock(new Block(22, "gold_ore"));
	public static final Block REDSTONE_ORE  = registerBlock(new Block(23, "redstone_ore"));
	public static final Block DIAMOND_ORE   = registerBlock(new Block(24, "diamond_ore"));
	
	public static final BlockPlant PLANT_DEADBUSH  = registerBlock(new BlockPlant(30, "plant_deadbush"));
	public static final BlockPlant PLANT_POPPY     = registerBlock(new BlockPlant(31, "plant_poppy"));
	public static final BlockPlant PLANT_DANDELION = registerBlock(new BlockPlant(32, "plant_dandelion"));
	
	public static final BlockFluid WATER = registerBlock(new BlockFluidWater(80, "water"));
	
	public static <T extends Block> T registerBlock(T block) {
		
		uidRegister.put(block.getUid(), block);
		identifierRegister.put(block.getIdentifier(), block);
		
		return block;
		
	}
	
	public static Block getBlock(short uid) {
		return uidRegister.get(uid);
	}
	
	public static Block getBlock(String identifier) {
		return identifierRegister.get(identifier);
	}
	
}
