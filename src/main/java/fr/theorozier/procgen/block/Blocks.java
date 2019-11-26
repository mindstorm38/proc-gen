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
