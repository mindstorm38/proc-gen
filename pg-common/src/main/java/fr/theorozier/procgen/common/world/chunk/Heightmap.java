package fr.theorozier.procgen.common.world.chunk;

import fr.theorozier.procgen.common.block.Blocks;
import fr.theorozier.procgen.common.block.state.BlockState;

import java.util.*;
import java.util.function.Predicate;

public class Heightmap {
	
	private static final Predicate<BlockState> IS_SET = Objects::nonNull;
	private static final Predicate<BlockState> IS_SET_NOT_WATER = b -> b != null && !b.isBlock(Blocks.WATER);
	
	private final WorldServerSection section;
	private final Predicate<BlockState> limitPredicate;
	private final short[] heights;
	
	public Heightmap(WorldServerSection section, Type type) {
	
		this.section = section;
		this.limitPredicate = type.limitPredicate;
		this.heights = new short[256];
		
	}
	
	public WorldServerSection getSection() {
		return this.section;
	}
	
	// All these methods are relative to the section
	
	public short get(int x, int z) {
		return this.heights[WorldSection.getSectionIndex(x, z)];
	}
	
	private void set(int x, int z, short height) {
		this.heights[WorldSection.getSectionIndex(x, z)] = height;
	}
	
	public enum Type {
	
		WORLD_BASE_SURFACE (IS_SET_NOT_WATER),
		WORLD_BASE_WATER_SURFACE (IS_SET),
		WORLD_SURFACE (IS_SET_NOT_WATER),
		WORLD_WATER_SURFACE (IS_SET);
	
		public final Predicate<BlockState> limitPredicate;
		
		Type(Predicate<BlockState> limitPredicate) {
			
			this.limitPredicate = limitPredicate;
			
		}
		
	}
	
	public static void updateSectionHeightmaps(WorldServerSection section, Set<Type> types) {
	
		int yStart = section.getWorld().getHeightLimit() - 1;
		
		BlockState block;
		Heightmap map;
		
		List<Heightmap> heightmaps = new ArrayList<>(types.size());
		ListIterator<Heightmap> iterator = heightmaps.listIterator();
		
		for (int x = 0; x < 16; ++x) {
			
			for (int z = 0; z < 16; ++z) {
				
				for (Type type : types)
					iterator.add(section.getHeightmap(type));
			
				for (int y = yStart; y >= 0; --y) {
					
					block = section.getBlockAt(x, y, z);
					
					while (iterator.hasNext()) {
						
						map = iterator.next();
						
						if (map.limitPredicate.test(block)) {
							
							map.set(x, z, (short) (y + 1));
							iterator.remove();
							
						}
						
					}
					
					if (heightmaps.isEmpty())
						break;
					
					while (iterator.hasPrevious())
						iterator.previous();
					
				}
				
				while (iterator.hasPrevious()) {
					iterator.previous();
					iterator.remove();
				}
			
			}
			
		}
	
	}
	
}
