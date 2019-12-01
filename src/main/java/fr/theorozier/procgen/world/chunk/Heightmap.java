package fr.theorozier.procgen.world.chunk;

import java.util.*;
import java.util.function.Predicate;

public class Heightmap {
	
	private static final Predicate<WorldBlock> IS_NOT_AIR = WorldBlock::isSet;
	
	private final Section section;
	private final Predicate<WorldBlock> limitPredicate;
	private final short[] heights;
	
	public Heightmap(Section section, Type type) {
	
		this.section = section;
		this.limitPredicate = type.limitPredicate;
		this.heights = new short[256];
		
	}
	
	// All these methods are relative to the section
	
	public short get(int x, int z) {
		return this.heights[Section.getHorizontalPositionIndex(x, z)];
	}
	
	private void set(int x, int z, short height) {
		this.heights[Section.getHorizontalPositionIndex(x, z)] = height;
	}
	
	public enum Type {
	
		WORLD_SURFACE (IS_NOT_AIR);
	
		public final Predicate<WorldBlock> limitPredicate;
		
		Type(Predicate<WorldBlock> limitPredicate) {
			
			this.limitPredicate = limitPredicate;
			
		}
		
	}
	
	public static void updateSectionHeightmaps(Section section, Set<Type> types) {
	
		int yStart = section.getWorld().getWorldHeightLimit() - 1;
		
		Chunk chunk;
		WorldBlock block;
		Heightmap map;
		
		List<Heightmap> heightmaps = new ArrayList<>(types.size());
		ListIterator<Heightmap> iterator = heightmaps.listIterator();
		
		for (int x = 0; x < 16; ++x) {
			
			for (int z = 0; z < 16; ++z) {
				
				for (Type type : types)
					iterator.add(section.getHeightmap(type));
			
				for (int y = yStart; y >= 0; --y) {
					
					chunk = section.getChunkAt(y);
					block = chunk.getBlockAtRelative(x, y & 15, z);
					
					if (block != null) {
						
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
					
				}
				
				while (iterator.hasPrevious()) {
					iterator.previous();
					iterator.remove();
				}
			
			}
			
		}
	
	}
	
}
